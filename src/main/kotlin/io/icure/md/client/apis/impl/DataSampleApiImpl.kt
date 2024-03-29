package io.icure.md.client.apis.impl

import com.github.benmanes.caffeine.cache.Caffeine
import io.icure.kraken.client.crypto.LocalCrypto
import io.icure.kraken.client.crypto.contactCryptoConfig
import io.icure.kraken.client.crypto.documentCryptoConfig
import io.icure.kraken.client.crypto.healthElementCryptoConfig
import io.icure.kraken.client.crypto.patientCryptoConfig
import io.icure.kraken.client.extendedapis.createContact
import io.icure.kraken.client.extendedapis.createDocument
import io.icure.kraken.client.extendedapis.deleteServices
import io.icure.kraken.client.extendedapis.filterContactsBy
import io.icure.kraken.client.extendedapis.filterServicesBy
import io.icure.kraken.client.extendedapis.getContact
import io.icure.kraken.client.extendedapis.getDocument
import io.icure.kraken.client.extendedapis.getHealthElements
import io.icure.kraken.client.extendedapis.getPatient
import io.icure.kraken.client.extendedapis.listServices
import io.icure.kraken.client.extendedapis.modifyContact
import io.icure.kraken.client.extendedapis.modifyDocument
import io.icure.kraken.client.extendedapis.setDocumentAttachment
import io.icure.kraken.client.models.DelegationDto
import io.icure.kraken.client.models.ListOfIdsDto
import io.icure.kraken.client.models.ServiceLinkDto
import io.icure.kraken.client.models.SubContactDto
import io.icure.kraken.client.models.UserDto
import io.icure.kraken.client.models.decrypted.ContactDto
import io.icure.kraken.client.models.decrypted.DocumentDto
import io.icure.kraken.client.models.decrypted.PatientDto
import io.icure.kraken.client.models.decrypted.ServiceDto
import io.icure.kraken.client.models.filter.chain.FilterChain
import io.icure.kraken.client.models.filter.contact.ContactByServiceIdsFilter
import io.icure.md.client.apis.DataSampleApi
import io.icure.md.client.apis.MedTechApi
import io.icure.md.client.filter.Filter
import io.icure.md.client.mappers.dataOwnerId
import io.icure.md.client.mappers.toAbstractFilterDto
import io.icure.md.client.mappers.toDataSample
import io.icure.md.client.mappers.toDocument
import io.icure.md.client.mappers.toPaginatedListDataSamples
import io.icure.md.client.mappers.toServiceDto
import io.icure.md.client.models.Content
import io.icure.md.client.models.DataSample
import io.icure.md.client.models.Document
import io.icure.md.client.models.PaginatedListDataSample
import io.icure.md.client.toHex
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.taktik.commons.uti.UTI
import org.taktik.commons.uti.impl.SimpleUTIDetector
import java.io.ByteArrayInputStream
import java.nio.ByteBuffer
import java.security.MessageDigest
import java.util.*
import java.util.concurrent.TimeUnit

@ExperimentalCoroutinesApi
@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
@FlowPreview
class DataSampleApiImpl(private val medTechApi: MedTechApi) : DataSampleApi {
    private val contactsCache = Caffeine.newBuilder()
        .expireAfterAccess(medTechApi.shortLivedCachesDuration, TimeUnit.SECONDS)
        .maximumSize(medTechApi.shortLivedCachesMaxSize)
        .build<Pair<String, String>, ContactDto>()

    private val utiDetector = SimpleUTIDetector()

    override suspend fun createOrModifyDataSampleFor(patientId: String, dataSample: DataSample): DataSample {
        return createOrModifyDataSamplesFor(patientId, listOf(dataSample)).first()
    }

    override suspend fun createOrModifyDataSamplesFor(
        patientId: String,
        dataSamples: List<DataSample>
    ): List<DataSample> {
        if (dataSamples.distinctBy { ds -> ds.batchId }.count() > 1) {
            throw IllegalArgumentException("Only data samples of a same batch can be processed together")
        }

        if (countHierarchyOfDataSamples(0, 0, dataSamples) > 1000) { // Arbitrary : 1 service = 1K
            throw IllegalArgumentException("Can't process more than 1000 data samples in the same batch")
        }

        val localCrypto = medTechApi.localCrypto
        val currentUser = medTechApi.baseUserApi.getCurrentUser()

        val (contactCached, existingContact) = getContactOfDataSample(localCrypto, currentUser, dataSamples.first())

        val contactPatientId = existingContact?.let {
            getPatientIdOfContact(localCrypto, currentUser, it)
                ?: throw IllegalArgumentException("Can't update a batch of data samples that is not linked to any patient yet.")
        }

        if (contactPatientId != null && contactPatientId != patientId) {
            throw IllegalArgumentException("Can't update the patient of a batch of data samples. Delete those samples and create new ones")
        }

        val existingPatient =
            medTechApi.basePatientApi.getPatient(currentUser, patientId, patientCryptoConfig(localCrypto))

        val createdOrModifiedContact = if (contactCached && existingContact != null) {
            val servicesToModify = dataSamples.map { it.toServiceDto() }
            val subContacts = createPotentialSubContactsForHealthElements(servicesToModify, currentUser)

            val contactToModify = existingContact.copy(
                services = servicesToModify.map { it.copy(healthElementsIds = null, formIds = null) },
                openingDate = servicesToModify.mapNotNull { it.openingDate ?: it.valueDate }
                    .minOfOrNull { it },
                closingDate = servicesToModify.mapNotNull { it.closingDate ?: it.valueDate }
                    .maxOfOrNull { it },
                subContacts = subContacts
            )

            medTechApi.baseContactApi.modifyContact(
                currentUser,
                contactToModify,
                contactCryptoConfig(localCrypto, currentUser)
            )
        } else {
            val contactToCreate = createContactDtoUsing(currentUser, dataSamples, existingContact)

            medTechApi.baseContactApi
                .createContact(
                    currentUser,
                    existingPatient,
                    contactToCreate,
                    contactCryptoConfig(localCrypto, currentUser)
                )
        }

        createdOrModifiedContact.services
            .forEach { contactsCache.put(currentUser.id to it.id, createdOrModifiedContact) }

        return createdOrModifiedContact.services
            .map {
                it.toDataSample(
                    batchId = createdOrModifiedContact.id,
                    responsible = createdOrModifiedContact.responsible,
                    subContacts = createdOrModifiedContact.subContacts.filter { subContactDto -> subContactDto.services.any { service -> service.serviceId == it.id } }
                )
            }
    }

    private suspend fun createPotentialSubContactsForHealthElements(
        servicesToCreate: List<ServiceDto>,
        currentUser: UserDto
    ): List<SubContactDto> {
        val servicesWithHE = servicesToCreate.filterNot { it.healthElementsIds.isNullOrEmpty() }
        return servicesWithHE.takeIf { it.isNotEmpty() }?.let {
            checkAndRetrieveProvidedHealthElements(
                it.flatMap { service -> service.healthElementsIds!! },
                currentUser
            ).map { heId ->
                SubContactDto(
                    healthElementId = heId,
                    services = servicesWithHE.filter { serviceWithHE -> serviceWithHE.healthElementsIds!!.contains(heId) }
                        .map { ServiceLinkDto(serviceId = it.id) }
                )
            }
        } ?: emptyList()
    }

    private suspend fun checkAndRetrieveProvidedHealthElements(
        healthElementIds: Collection<String>,
        currentUser: UserDto
    ): List<String> {
        if (healthElementIds.isEmpty()) {
            return emptyList()
        }
        val distinctHealthElementIds = healthElementIds.distinct()
        return medTechApi.baseHealthElementApi.getHealthElements(
            currentUser,
            ListOfIdsDto(healthElementIds.toList()),
            healthElementCryptoConfig(medTechApi.localCrypto)
        ).map { it.id }.also { foundHealthElementIds ->
            if (foundHealthElementIds.size != distinctHealthElementIds.size) {
                throw IllegalStateException(
                    "Health elements [${
                    (distinctHealthElementIds - foundHealthElementIds).joinToString(
                        ", "
                    )
                    }] do not exist or user ${currentUser.id} may not access them"
                )
            }
        }
    }

    /**
     * @return A boolean to know if the contact was cached or not, and the Contact containing the provided data sample.
     */
    private suspend fun getContactOfDataSample(
        localCrypto: LocalCrypto,
        currentUser: UserDto,
        dataSample: DataSample,
        byPassCache: Boolean = false
    ): Pair<Boolean, ContactDto?> {
        return dataSample.id
            ?.takeUnless { byPassCache }
            ?.let { contactsCache.getIfPresent(currentUser.id to it) }
            ?.let { true to it }
            ?: (
                false to dataSample.batchId?.let { contactId ->
                    getContactFromICure(
                        localCrypto,
                        currentUser,
                        contactId
                    )
                }
                )
    }

    private suspend fun getContactFromICure(
        localCrypto: LocalCrypto,
        currentUser: UserDto,
        contactId: String
    ): ContactDto {
        return medTechApi.baseContactApi.getContact(
            currentUser,
            contactId,
            contactCryptoConfig(localCrypto, currentUser)
        )
    }

    private suspend fun getPatientIdOfContact(
        localCrypto: LocalCrypto,
        currentUser: UserDto,
        contact: ContactDto
    ) = localCrypto.decryptEncryptionKeys(currentUser.dataOwnerId(), contact.cryptedForeignKeys)
        .firstOrNull()

    private suspend fun getPatientOfContact(
        localCrypto: LocalCrypto,
        currentUser: UserDto,
        contact: ContactDto
    ): PatientDto? {
        return getPatientIdOfContact(localCrypto, currentUser, contact)
            ?.let { medTechApi.basePatientApi.getPatient(currentUser, it, patientCryptoConfig(localCrypto)) }
    }

    private fun countHierarchyOfDataSamples(
        currentCount: Int,
        dataSampleIndex: Int,
        dataSamples: List<DataSample>
    ): Int {
        if (dataSampleIndex >= dataSamples.size) {
            return currentCount
        }

        val currentDataSample = dataSamples[dataSampleIndex]
        val dataSampleCount = currentDataSample.content.values
            .filterNot { it.compoundValue.isNullOrEmpty() }
            .sumOf { countHierarchyOfDataSamples(0, 0, it.compoundValue!!) }

        return countHierarchyOfDataSamples(currentCount + dataSampleCount, dataSampleIndex + 1, dataSamples)
    }

    private suspend fun createContactDtoUsing(
        currentUser: UserDto,
        dataSamples: List<DataSample>,
        existingContact: ContactDto? = null
    ): ContactDto {
        val servicesToCreate = dataSamples.map { it.toServiceDto().copy(modified = null) }
        val subContacts = createPotentialSubContactsForHealthElements(servicesToCreate, currentUser)
        val baseContact =
            existingContact?.copy(id = UUID.randomUUID().toString(), rev = null, modified = System.currentTimeMillis())
                ?: ContactDto(id = UUID.randomUUID().toString())

        return baseContact.copy(
            services = servicesToCreate,
            subContacts = subContacts,
            openingDate = servicesToCreate.mapNotNull { it.openingDate ?: it.valueDate }
                .minOfOrNull { it },
            closingDate = servicesToCreate.mapNotNull { it.closingDate ?: it.valueDate }
                .maxOfOrNull { it }
        )
    }

    override suspend fun deleteAttachment(dataSampleId: String, documentId: String): String {
        val localCrypto = medTechApi.localCrypto
        val currentUser = medTechApi.baseUserApi.getCurrentUser()

        val existingContact = findContactsForDataSampleIds(currentUser, localCrypto, listOf(dataSampleId))
            .firstOrNull()
            ?: throw RuntimeException("Could not find batch information of the data sample $dataSampleId")

        val existingService = existingContact.services.find { it.id == dataSampleId }
            ?: throw RuntimeException("Could not find data sample $dataSampleId")

        val contactPatientId = getPatientIdOfContact(localCrypto, currentUser, existingContact)
            ?: throw IllegalArgumentException("Can not set an attachment to a data sample not linked to a patient")

        val contentToDelete = existingService.content.entries.find { (_, content) -> content.documentId == documentId }
            ?.key
            ?: throw IllegalArgumentException("Id $documentId does not reference any document in the data sample $dataSampleId")

        createOrModifyDataSampleFor(
            contactPatientId,
            existingService.copy(content = existingService.content.filterKeys { it != contentToDelete }).toDataSample()
        )

        return documentId
    }

    override suspend fun deleteDataSample(dataSampleId: String): String {
        return deleteDataSamples(listOf(dataSampleId)).firstOrNull()
            ?: throw RuntimeException("Couldn't delete data sample $dataSampleId")
    }

    override suspend fun deleteDataSamples(dataSampleIds: List<String>): List<String> {
        val localCrypto = medTechApi.localCrypto
        val currentUser = medTechApi.baseUserApi.getCurrentUser()

        val existingContact = findContactsForDataSampleIds(currentUser, localCrypto, dataSampleIds)
            .firstOrNull()
            ?: throw RuntimeException("Could not find batch information of data samples $dataSampleIds")
        val existingServiceIds = existingContact.services.map { serv -> serv.id }

        if (dataSampleIds.any { it !in existingServiceIds }) {
            throw RuntimeException("Could not find all data samples in same batch ${existingContact.id}")
        }

        val contactPatient = getPatientOfContact(localCrypto, currentUser, existingContact)
            ?: throw RuntimeException("Couldn't find patient related to batch of data samples ${existingContact.id}")

        val servicesToDelete = existingContact.services.filter { it.id in dataSampleIds }

        return medTechApi.baseContactApi.deleteServices(
            currentUser,
            contactPatient,
            servicesToDelete,
            contactCryptoConfig(localCrypto, currentUser)
        )
            .services
            .filter { it.id in dataSampleIds }
            .filter { it.endOfLife != null }
            .map { it.id }
    }

    private suspend fun findContactsForDataSampleIds(
        currentUser: UserDto,
        localCrypto: LocalCrypto,
        dataSampleIds: List<String>
    ): Set<ContactDto> {
        val cachedContacts = contactsCache.getAllPresent(dataSampleIds.map { currentUser.id to it })
        val dataSampleIdsToSearch = dataSampleIds
            .filterNot { cachedContacts.containsKey(currentUser.id to it) }

        return if (dataSampleIdsToSearch.isNotEmpty()) {
            val notCachedContacts = medTechApi.baseContactApi
                .filterContactsBy(
                    currentUser,
                    FilterChain(ContactByServiceIdsFilter(ids = dataSampleIdsToSearch)),
                    null,
                    null,
                    dataSampleIds.size,
                    contactCryptoConfig(localCrypto, currentUser)
                )
                .rows
                .sortedByDescending { it.modified }

            dataSampleIdsToSearch.forEach { dataSampleId ->
                notCachedContacts
                    .find { contact -> dataSampleId in contact.services.map { s -> s.id } }
                    ?.let { contactsCache.put(currentUser.id to dataSampleId, it) }
            }

            (cachedContacts.values + notCachedContacts).toSet()
        } else {
            cachedContacts.values.toSet()
        }
    }

    override suspend fun filterDataSamples(
        filter: Filter<DataSample>,
        nextDataSampleId: String?,
        limit: Int?
    ): PaginatedListDataSample {
        val currentUser = medTechApi.baseUserApi.getCurrentUser()
        val localCrypto = medTechApi.localCrypto

        return medTechApi.baseContactApi
            .filterServicesBy(
                currentUser,
                FilterChain(filter.toAbstractFilterDto(), null),
                nextDataSampleId,
                limit,
                contactCryptoConfig(localCrypto, currentUser).crypto
            )
            .toPaginatedListDataSamples()
    }

    override suspend fun getDataSample(dataSampleId: String): DataSample {
        return getServiceFromICure(dataSampleId)
            ?.toDataSample()
            ?: throw IllegalArgumentException("Id $dataSampleId does not correspond to any existing data sample")
    }

    private suspend fun getServiceFromICure(dataSampleId: String): ServiceDto? {
        val localCrypto = medTechApi.localCrypto
        val currentUser = medTechApi.baseUserApi.getCurrentUser()

        return medTechApi.baseContactApi.listServices(
            currentUser,
            ListOfIdsDto(listOf(dataSampleId)),
            contactCryptoConfig(localCrypto, currentUser).crypto
        )
            .firstOrNull()
    }

    override suspend fun getDataSampleAttachmentDocument(dataSampleId: String, documentId: String): Document {
        val localCrypto = medTechApi.localCrypto
        val currentUser = medTechApi.baseUserApi.getCurrentUser()

        return getDataSampleAttachmentDocumentFromICure(localCrypto, currentUser, dataSampleId, documentId)
            .toDocument()
    }

    override suspend fun getDataSampleAttachmentContent(
        dataSampleId: String,
        documentId: String,
        attachmentId: String
    ): Flow<ByteBuffer> {
        val localCrypto = medTechApi.localCrypto
        val currentUser = medTechApi.baseUserApi.getCurrentUser()

        val documentOfAttachment =
            getDataSampleAttachmentDocumentFromICure(localCrypto, currentUser, dataSampleId, documentId)

        return medTechApi.baseDocumentApi.getDocumentAttachment(
            documentId,
            attachmentId,
            getDocumentEncryptionKeys(localCrypto, currentUser, documentOfAttachment).joinToString(","),
            null
        )
    }

    private suspend fun getDataSampleAttachmentDocumentFromICure(
        localCrypto: LocalCrypto,
        currentUser: UserDto,
        dataSampleId: String,
        documentId: String
    ): DocumentDto {
        val existingDataSample = getDataSample(dataSampleId)

        if (existingDataSample.content.entries.find { (_, content) -> content.documentId == documentId } == null) {
            throw IllegalArgumentException("Id $documentId does not reference any document in the data sample $dataSampleId")
        }

        return medTechApi.baseDocumentApi.getDocument(currentUser, documentId, documentCryptoConfig(localCrypto))
    }

    override suspend fun matchDataSamples(filter: Filter<DataSample>): List<String> {
        return medTechApi.baseContactApi.matchServicesBy(filter.toAbstractFilterDto())
    }

    override suspend fun setDataSampleAttachment(
        dataSampleId: String,
        body: Flow<ByteBuffer>,
        documentName: String?,
        documentVersion: String?,
        documentExternalUuid: String?,
        documentLanguage: String?
    ): Document {
        val localCrypto = medTechApi.localCrypto
        val currentUser = medTechApi.baseUserApi.getCurrentUser()

        val existingDataSample = getDataSample(dataSampleId)
        val existingContact = getContactOfDataSample(localCrypto, currentUser, existingDataSample)
            .second
            ?: throw RuntimeException("Could not find batch information of the data sample $dataSampleId")

        val patientOfContact = getPatientIdOfContact(localCrypto, currentUser, existingContact)
            ?: throw IllegalArgumentException("Can not set an attachment to a data sample not linked to a patient")

        val documentToCreate = DocumentDto(
            id = UUID.randomUUID().toString(),
            name = documentName,
            version = documentVersion,
            externalUuid = documentExternalUuid
        )

        val documentConfig = documentCryptoConfig(localCrypto)
        val md = MessageDigest.getInstance("SHA-256")
        var uti: UTI? = null

        val createdDocument = medTechApi.baseDocumentApi.createDocument(currentUser, documentToCreate, documentConfig)
            .let { createdDoc ->
                // Updating data sample with docId
                val contentIso = documentLanguage ?: medTechApi.defaultLanguage
                createOrModifyDataSampleFor(
                    patientOfContact,
                    existingDataSample.copy(content = mapOf(contentIso to Content(documentId = createdDoc.id)))
                )

                // Adding attachment to document
                medTechApi.baseDocumentApi.setDocumentAttachment(
                    user = currentUser,
                    documentId = createdDoc.id,
                    requestBody = body.map {
                        if (uti == null) {
                            val byteArray = ByteArray(it.remaining().coerceAtMost(256))
                            it.slice().get(byteArray, 0, byteArray.size)
                            uti = utiDetector.detectUTI(ByteArrayInputStream(byteArray), null, null)
                        }
                        md.update(it.slice())
                        it
                    },
                    enckeys = getDocumentEncryptionKeys(localCrypto, currentUser, createdDoc).firstOrNull(),
                    config = documentConfig
                )
            }

        // Add the hash and UTI of the document
        val finalDoc = medTechApi.baseDocumentApi
            .modifyDocument(
                currentUser,
                createdDocument.copy(hash = md.digest().toHex(), mainUti = uti.toString()),
                documentConfig
            )

        return finalDoc.toDocument()
    }

    override suspend fun giveAccessTo(dataSample: DataSample, delegateTo: String): DataSample {
        val localCrypto = medTechApi.localCrypto
        val currentUser = medTechApi.baseUserApi.getCurrentUser()
        val dataOwnerId = currentUser.dataOwnerId()
        val (_, contact) = getContactOfDataSample(localCrypto, currentUser, dataSample, true)

        if (!contact!!.delegations.keys.any { it == dataOwnerId }) {
            throw IllegalStateException("DataOwner $dataOwnerId does not have the right to access contact ${contact.id}")
        }

        if (contact.delegations.keys.any { it == dataOwnerId }) {
            return dataSample
        }

        val ccContact = contactCryptoConfig(localCrypto, currentUser)

        val (patientIdKey, _) = localCrypto.encryptAESKeyForDataOwner(
            dataOwnerId,
            delegateTo,
            contact.id,
            localCrypto.decryptEncryptionKeys(dataOwnerId, contact.cryptedForeignKeys).first()
        )
        val (secretForeignKey, _) = localCrypto.encryptAESKeyForDataOwner(
            dataOwnerId,
            delegateTo,
            contact.id,
            localCrypto.decryptEncryptionKeys(dataOwnerId, contact.delegations).first()
        )
        val (encryptionKey, _) = localCrypto.encryptAESKeyForDataOwner(
            dataOwnerId,
            delegateTo,
            contact.id,
            localCrypto.decryptEncryptionKeys(dataOwnerId, contact.encryptionKeys).first()
        )

        val delegation = DelegationDto(owner = dataOwnerId, delegatedTo = delegateTo, key = secretForeignKey)
        val encryptionKeyDelegation = DelegationDto(owner = dataOwnerId, delegatedTo = delegateTo, key = encryptionKey)
        val cryptedForeignKeyDelegation =
            DelegationDto(owner = dataOwnerId, delegatedTo = delegateTo, key = patientIdKey)

        val delegations = contact.delegations.plus(delegateTo to setOf(delegation))
        val encryptionKeys = contact.encryptionKeys.plus(delegateTo to setOf(encryptionKeyDelegation))
        val cryptedForeignKeys = contact.cryptedForeignKeys.plus(delegateTo to setOf(cryptedForeignKeyDelegation))

        val contactToUpdate = contact.copy(
            delegations = delegations,
            encryptionKeys = encryptionKeys,
            cryptedForeignKeys = cryptedForeignKeys
        )

        val updatedContact = medTechApi.baseContactApi.modifyContact(currentUser, contactToUpdate, ccContact)
        return updatedContact.services.singleOrNull()?.toDataSample(updatedContact.id)
            ?: throw IllegalStateException("Couldn't give access to $delegateTo to dataSample ${dataSample.id}")
    }

    private suspend fun getDocumentEncryptionKeys(
        localCrypto: LocalCrypto,
        currentUser: UserDto,
        document: DocumentDto
    ) = localCrypto.decryptEncryptionKeys(currentUser.dataOwnerId(), document.encryptionKeys)
}

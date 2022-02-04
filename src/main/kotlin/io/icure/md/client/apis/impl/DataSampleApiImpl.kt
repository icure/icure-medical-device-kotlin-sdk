package io.icure.md.client.apis.impl

import io.icure.kraken.client.crypto.contactCryptoConfig
import io.icure.kraken.client.crypto.patientCryptoConfig
import io.icure.kraken.client.extendedapis.createContact
import io.icure.kraken.client.extendedapis.getContact
import io.icure.kraken.client.extendedapis.getPatient
import io.icure.kraken.client.models.decrypted.ContactDto
import io.icure.md.client.apis.DataSampleApi
import io.icure.md.client.apis.MedTechApi
import io.icure.md.client.mappers.findIdForEncryptionKey
import io.icure.md.client.mappers.toDataSample
import io.icure.md.client.mappers.toServiceDto
import io.icure.md.client.models.DataSample
import io.icure.md.client.models.Document
import io.icure.md.client.models.Filter
import io.icure.md.client.models.PaginatedListDataSample
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import java.nio.ByteBuffer
import java.util.*

@ExperimentalCoroutinesApi
@ExperimentalStdlibApi
class DataSampleApiImpl(private val medTechApi: MedTechApi) : DataSampleApi {

    override suspend fun createOrModifyDataSampleFor(patientId: String, dataSample: DataSample): DataSample {
        return createOrModifyDataSamplesFor(patientId, listOf(dataSample)).first()
    }

    override suspend fun createOrModifyDataSamplesFor(
        patientId: String,
        dataSample: List<DataSample>
    ): List<DataSample> {
        if (dataSample.distinctBy { ds -> ds.batchId }.count() > 1) {
            throw IllegalArgumentException("Only data samples of a same batch can be processed together")
        }

        if (countHierarchyOfDataSamples(0, 0, dataSample) > 1000) { // Arbitrary : 1 service = 1K
            throw IllegalArgumentException("Can't process more than 1000 data samples in the same batch")
        }

        val localCrypto = medTechApi.localCrypto()
        val currentUser = medTechApi.userApi().getCurrentUser()
        val existingContact = dataSample.first().batchId?.let { contactId ->
            medTechApi.contactApi().getContact(currentUser, contactId, contactCryptoConfig(localCrypto, currentUser))
        }

        val contactPatientId = existingContact?.let {
            localCrypto.decryptEncryptionKeys(currentUser.findIdForEncryptionKey(), it.cryptedForeignKeys)
                .firstOrNull()
        }

        if (contactPatientId != null && contactPatientId != patientId) {
            throw IllegalArgumentException("Can't update the patient of a batch of data samples. Delete those samples and create new ones")
        }

        val existingPatient =
            medTechApi.patientApi().getPatient(currentUser, patientId, patientCryptoConfig(localCrypto))
        val contactToCreate = createContactDtoBasedOn(dataSample, existingContact)

        return medTechApi.contactApi()
            .createContact(currentUser, existingPatient, contactToCreate, contactCryptoConfig(localCrypto, currentUser))
            .let { createdContact ->
                createdContact.services.map { it.toDataSample(batchId = contactToCreate.id) }
            }
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

    private fun createContactDtoBasedOn(
        dataSamples: List<DataSample>,
        existingContact: ContactDto? = null
    ): ContactDto {
        val servicesToCreate = dataSamples.map { it.toServiceDto() }
        val baseContact =
            existingContact?.copy(id = UUID.randomUUID().toString(), rev = null, modified = System.currentTimeMillis())
                ?: ContactDto(id = UUID.randomUUID().toString())

        return baseContact.copy(
            services = servicesToCreate,
            openingDate = servicesToCreate.mapNotNull { it.openingDate ?: it.valueDate }
                .minOfOrNull { it },
            closingDate = servicesToCreate.mapNotNull { it.closingDate ?: it.valueDate }
                .maxOfOrNull { it }
        )
    }

    override suspend fun deleteAttachment(dataSampleId: String): String {
        TODO("Not yet implemented")
    }

    override suspend fun deleteDataSample(dataSampleId: String): String {
        TODO("Not yet implemented")
    }

    override suspend fun deleteDataSamples(dataSampleIds: List<String>): List<String> {
        TODO("Not yet implemented")
    }

    override suspend fun filterDataSample(filter: Filter): PaginatedListDataSample {
        TODO("Not yet implemented")
    }

    override suspend fun getDataSample(dataSampleId: String): DataSample {
        TODO("Not yet implemented")
    }

    override suspend fun getDataSampleAttachment(dataSampleId: String): Document {
        TODO("Not yet implemented")
    }

    override suspend fun getDataSampleAttachmentContent(
        dataSampleId: String,
        attachmentId: String
    ): List<Any> {
        TODO("Not yet implemented")
    }

    override suspend fun matchDataSample(filter: Filter): List<String> {
        TODO("Not yet implemented")
    }

    override suspend fun setDataSampleAttachment(
        dataSampleId: String,
        body: Flow<ByteBuffer>,
        documentName: String?,
        documentVersion: String?,
        documentExternalUuid: String?
    ): Document {
        TODO("Not yet implemented")
    }
}

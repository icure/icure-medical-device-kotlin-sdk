/**
 * ICure Medical Device Micro Service
 *
 * ICure Medical Device Micro Service
 *
 * The version of the OpenAPI document: v2
 *
 *
 * Please note:
 * This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * Do not edit this file manually.
 */
package io.icure.md.client.filter

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import io.icure.kraken.client.crypto.LocalCrypto
import io.icure.md.client.filter.coding.AllCodingsFilter
import io.icure.md.client.filter.coding.CodingByIdsFilter
import io.icure.md.client.filter.datasample.DataSampleByHealthcarePartyFilter
import io.icure.md.client.filter.datasample.DataSampleByHealthcarePartyHealthcareElementIdsFilter
import io.icure.md.client.filter.datasample.DataSampleByHealthcarePartyIdentifiersFilter
import io.icure.md.client.filter.datasample.DataSampleByHealthcarePartyLabelCodeDateFilter
import io.icure.md.client.filter.datasample.DataSampleByIdsFilter
import io.icure.md.client.filter.datasample.DataSampleByPatientFilter
import io.icure.md.client.filter.hcp.AllHealthcareProfessionalsFilter
import io.icure.md.client.filter.hcp.HealthcareProfessionalByIdsFilter
import io.icure.md.client.filter.healthcareelement.HealthcareElementByHealthcarePartyFilter
import io.icure.md.client.filter.healthcareelement.HealthcareElementByHealthcarePartyIdentifiersFilter
import io.icure.md.client.filter.healthcareelement.HealthcareElementByHealthcarePartyLabelCodeFilter
import io.icure.md.client.filter.healthcareelement.HealthcareElementByHealthcarePartyPatientFilter
import io.icure.md.client.filter.healthcareelement.HealthcareElementByIdsFilter
import io.icure.md.client.filter.medicaldevice.AllMedicalDevicesFilter
import io.icure.md.client.filter.medicaldevice.MedicalDeviceByIdsFilter
import io.icure.md.client.filter.patient.PatientByHealthcarePartyAndIdentifiersFilter
import io.icure.md.client.filter.patient.PatientByHealthcarePartyDateOfBirthBetweenFilter
import io.icure.md.client.filter.patient.PatientByHealthcarePartyFilter
import io.icure.md.client.filter.patient.PatientByHealthcarePartyGenderEducationProfession
import io.icure.md.client.filter.patient.PatientByHealthcarePartyNameContainsFuzzyFilter
import io.icure.md.client.filter.patient.PatientByHealthcarePartyNameFilter
import io.icure.md.client.filter.patient.PatientByIdsFilter
import io.icure.md.client.filter.user.AllUsersFilter
import io.icure.md.client.filter.user.UserByIdsFilter
import io.icure.md.client.mappers.toDelegationDto
import io.icure.md.client.models.Coding
import io.icure.md.client.models.DataSample
import io.icure.md.client.models.HealthcareElement
import io.icure.md.client.models.HealthcareProfessional
import io.icure.md.client.models.Identifier
import io.icure.md.client.models.MedicalDevice
import io.icure.md.client.models.Patient
import io.icure.md.client.models.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalUnit

/**
 *
 *
 * @param description
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
interface Filter<T> {
    val description: String?
}

fun <T> filter(init: FilterBuilder<T>.() -> Unit): FilterBuilder<T> {
    val filter = FilterBuilder<T>()
    filter.init()
    return filter
}

open class FilterBuilder<T>(val parent: FilterBuilder<T>? = null) {
    var dataOwnerId: String? = null
    var proxiedFilterBuilder: ((dataOwnerId: String?) -> Filter<T>)? = null

    fun forDataOwner(dataOwnerId: String?): FilterBuilder<T> {
        this.dataOwnerId = dataOwnerId
        return this
    }

    fun dataOwner(): String? {
        return dataOwnerId ?: parent?.dataOwner()
    }

    open fun registerInParent(builder: (dataOwnerId: String?) -> Filter<T>) {
        proxiedFilterBuilder = builder
    }

    open fun build(): Filter<T> {
        return proxiedFilterBuilder?.let { it(dataOwnerId) }
            ?: throw IllegalArgumentException("At least one condition must be set for this filter")
    }
}

fun <T> FilterBuilder<T>.union(init: UnionFilterBuilder<T>.() -> Unit): FilterBuilder<T> {
    val filter = UnionFilterBuilder(this)
    filter.init()
    this.registerInParent { dataOwnerId -> filter.forDataOwner(dataOwnerId).build() }
    return filter
}

fun <T> FilterBuilder<T>.intersection(init: IntersectionFilterBuilder<T>.() -> Unit): FilterBuilder<T> {
    val filter = IntersectionFilterBuilder(this)
    filter.init()
    this.registerInParent { dataOwnerId -> filter.forDataOwner(dataOwnerId).build() }
    return filter
}

open class CompoundFilterBuilder<T>(parent: FilterBuilder<T>? = null) : FilterBuilder<T>(parent) {
    var compoundedFilterBuilders: List<(dataOwnerId: String?) -> Filter<T>> = emptyList()
    override fun registerInParent(builder: (dataOwnerId: String?) -> Filter<T>) {
        compoundedFilterBuilders = compoundedFilterBuilders + builder
    }
}

class UnionFilterBuilder<T>(parent: FilterBuilder<T>? = null) : CompoundFilterBuilder<T>(parent) {
    override fun build() = UnionFilter(null, compoundedFilterBuilders.map { it(dataOwner()) })
}

class IntersectionFilterBuilder<T>(parent: FilterBuilder<T>? = null) : CompoundFilterBuilder<T>(parent) {
    override fun build() = IntersectionFilter(null, compoundedFilterBuilders.map { it(dataOwner()) })
}

inline fun <reified T : Any> FilterBuilder<T>.all() = when (T::class) {
    Coding::class -> (this as FilterBuilder<Coding>).allCodings()
    DataSample::class -> (this as FilterBuilder<DataSample>).allDataSamples()
    MedicalDevice::class -> (this as FilterBuilder<MedicalDevice>).allMedicalDevices()
    HealthcareProfessional::class -> (this as FilterBuilder<HealthcareProfessional>).allHealthcareProfessionals()
    HealthcareElement::class -> (this as FilterBuilder<HealthcareElement>).allHealthcareElements()
    Patient::class -> (this as FilterBuilder<Patient>).allPatients()
    User::class -> (this as FilterBuilder<User>).allUsers()
    else -> throw IllegalArgumentException("All is not supported fot ${T::class}")
}

inline fun <reified T : Any> FilterBuilder<T>.byIds(vararg ids: String) = when (T::class) {
    Coding::class -> (this as FilterBuilder<Coding>).codingsByIds(*ids)
    DataSample::class -> (this as FilterBuilder<DataSample>).dataSamplesByIds(*ids)
    MedicalDevice::class -> (this as FilterBuilder<MedicalDevice>).medicalDevicesByIds(*ids)
    HealthcareProfessional::class -> (this as FilterBuilder<HealthcareProfessional>).healthcareProfessionalsByIds(*ids)
    HealthcareElement::class -> (this as FilterBuilder<HealthcareElement>).healthcareElementsByIds(*ids)
    Patient::class -> (this as FilterBuilder<Patient>).patientsByIds(*ids)
    User::class -> (this as FilterBuilder<User>).usersByIds(*ids)
    else -> throw IllegalArgumentException("All is not supported fot ${T::class}")
}

inline fun <reified T : Any> FilterBuilder<T>.byIdentifiers(vararg identifiers: Identifier) = when (T::class) {
    DataSample::class -> (this as FilterBuilder<DataSample>).dataSamplesByIdentifiers(*identifiers)
    HealthcareElement::class -> (this as FilterBuilder<HealthcareElement>).healthcareElementsByIdentifiers(*identifiers)
    Patient::class -> (this as FilterBuilder<Patient>).patientsByIdentifiers(*identifiers)
    else -> throw IllegalArgumentException("All is not supported fot ${T::class}")
}

inline fun <reified T : Any> FilterBuilder<T>.byHealthcareElementIds(vararg healthcareElementIds: String) =
    when (T::class) {
        DataSample::class -> (this as FilterBuilder<DataSample>).dataSamplesByHealthcareElementIds(*healthcareElementIds)
        else -> throw IllegalArgumentException("All is not supported fot ${T::class}")
    }

inline fun <reified T : Any> FilterBuilder<T>.withLabel(type: String, code: String?) = when (T::class) {
    DataSample::class -> (this as FilterBuilder<DataSample>).dataSamplesWithLabel(type, code)
    HealthcareElement::class -> (this as FilterBuilder<HealthcareElement>).healthcareElementsWithLabel(type, code)
    else -> throw IllegalArgumentException("withLabel is not supported fot ${T::class}")
}

inline fun <reified T : Any> FilterBuilder<T>.withCode(type: String, code: String?) = when (T::class) {
    DataSample::class -> (this as FilterBuilder<DataSample>).dataSamplesWithCode(type, code)
    HealthcareElement::class -> (this as FilterBuilder<HealthcareElement>).healthcareElementsWithCode(type, code)
    else -> throw IllegalArgumentException("withCode is not supported fot ${T::class}")
}

@ExperimentalCoroutinesApi
@ExperimentalStdlibApi
suspend inline fun <reified T : Any> FilterBuilder<T>.ofPatients(localCrypto: LocalCrypto, vararg patients: Patient) =
    when (T::class) {
        DataSample::class -> (this as FilterBuilder<DataSample>).dataSamplesOfPatients(localCrypto, *patients)
        HealthcareElement::class -> (this as FilterBuilder<HealthcareElement>).healthcareElementsOfPatients(
            localCrypto,
            *patients
        )
        else -> throw IllegalArgumentException("ofPatient is not supported fot ${T::class}")
    }

fun FilterBuilder<HealthcareElement>.healthcareElementsWithLabel(type: String, code: String?) {
    this.registerInParent { dataOwnerId ->
        HealthcareElementByHealthcarePartyLabelCodeFilter(
            null,
            dataOwnerId,
            type,
            code,
            null,
            null
        )
    }
}

fun FilterBuilder<DataSample>.dataSamplesWithLabel(type: String, code: String?) {
    this.registerInParent { dataOwnerId ->
        DataSampleByHealthcarePartyLabelCodeDateFilter(
            null,
            dataOwnerId,
            null,
            type,
            code,
            null,
            null
        )
    }
}

fun FilterBuilder<DataSample>.dataSamplesWithCode(type: String, code: String?) {
    this.registerInParent { dataOwnerId ->
        DataSampleByHealthcarePartyLabelCodeDateFilter(
            null,
            dataOwnerId,
            null,
            null,
            null,
            type,
            code
        )
    }
}

fun FilterBuilder<HealthcareElement>.healthcareElementsWithCode(type: String, code: String?) {
    this.registerInParent { dataOwnerId ->
        HealthcareElementByHealthcarePartyLabelCodeFilter(
            null,
            dataOwnerId,
            null,
            null,
            type,
            code
        )
    }
}

@ExperimentalCoroutinesApi
@ExperimentalStdlibApi
suspend fun FilterBuilder<DataSample>.dataSamplesOfPatients(localCrypto: LocalCrypto, vararg patients: Patient) {
    this.registerInParent { dataOwnerId ->
        DataSampleByPatientFilter(
            null,
            dataOwnerId
                ?: throw IllegalArgumentException("DataSampleByPatientFilter needs a hcp to be registered in the builder using a forDataOwner call"),
            patients.toSet().flatMap {
                localCrypto.decryptEncryptionKeys(
                    dataOwnerId,
                    it.systemMetaData?.delegations?.mapValues { (k, v) -> v.map { it.toDelegationDto() }.toSet() }
                        ?: emptyMap()
                )
            }.toSet()
        )
    }
}

@ExperimentalCoroutinesApi
@ExperimentalStdlibApi
suspend fun FilterBuilder<HealthcareElement>.healthcareElementsOfPatients(
    localCrypto: LocalCrypto,
    vararg patients: Patient
) {
    this.registerInParent { dataOwnerId ->
        HealthcareElementByHealthcarePartyPatientFilter(
            null,
            dataOwnerId
                ?: throw IllegalArgumentException("HealthcareElementByHealthcarePartyPatientFilter needs a dataOwner to be registered in the builder using a forDataOwner call"),
            patients.flatMap {
                localCrypto.decryptEncryptionKeys(
                    dataOwnerId,
                    it.systemMetaData?.delegations?.mapValues { (k, v) -> v.map { it.toDelegationDto() }.toSet() }
                        ?: emptyMap()
                )
            }.toSet()
        )
    }
}

fun FilterBuilder<Coding>.allCodings() {
    this.registerInParent { dataOwnerId -> AllCodingsFilter(null) }
}

fun FilterBuilder<Coding>.codingsByIds(vararg ids: String) {
    this.registerInParent { dataOwnerId -> CodingByIdsFilter(null, ids.toSet()) }
}

fun FilterBuilder<DataSample>.allDataSamples() {
    this.registerInParent { dataOwnerId ->
        DataSampleByHealthcarePartyFilter(
            dataOwnerId
                ?: throw IllegalArgumentException("DataSampleByHealthcarePartyFilter needs a dataOwner to be registered in the builder using a forDataOwner call"),
            null
        )
    }
}

fun FilterBuilder<DataSample>.dataSamplesByIds(vararg ids: String) {
    this.registerInParent { dataOwnerId -> DataSampleByIdsFilter(ids.toSet(), null) }
}

fun FilterBuilder<DataSample>.dataSamplesByIdentifiers(vararg identifiers: Identifier) {
    this.registerInParent { dataOwnerId ->
        DataSampleByHealthcarePartyIdentifiersFilter(
            healthcarePartyId = dataOwnerId
                ?: throw IllegalArgumentException("DataSampleByHealthcarePartyAndIdentifiersFilter needs a dataOwner to be registered in the builder using a forDataOwner call"),
            identifiers = identifiers.toList()
        )
    }
}

fun FilterBuilder<DataSample>.dataSamplesByHealthcareElementIds(vararg healthcareElementIds: String) {
    this.registerInParent { dataOwnerId ->
        DataSampleByHealthcarePartyHealthcareElementIdsFilter(
            healthcarePartyId = dataOwnerId
                ?: throw IllegalArgumentException("DataSampleByHealthcarePartyHealthcareElementIdsFilter needs a dataOwner to be registered in the builder using a forDataOwner call"),
            healthcareElementIds = healthcareElementIds.toList()
        )
    }
}

fun FilterBuilder<MedicalDevice>.allMedicalDevices() {
    this.registerInParent { dataOwnerId -> AllMedicalDevicesFilter(null) }
}

fun FilterBuilder<MedicalDevice>.medicalDevicesByIds(vararg ids: String) {
    this.registerInParent { dataOwnerId -> MedicalDeviceByIdsFilter(ids.toSet(), null) }
}

fun FilterBuilder<HealthcareProfessional>.allHealthcareProfessionals() {
    this.registerInParent { dataOwnerId -> AllHealthcareProfessionalsFilter(null) }
}

fun FilterBuilder<HealthcareProfessional>.healthcareProfessionalsByIds(vararg ids: String) {
    this.registerInParent { dataOwnerId -> HealthcareProfessionalByIdsFilter(ids.toSet(), null) }
}

fun FilterBuilder<HealthcareElement>.allHealthcareElements() {
    this.registerInParent { dataOwnerId ->
        HealthcareElementByHealthcarePartyFilter(
            dataOwnerId
                ?: throw IllegalArgumentException("HealthcareElementByHealthcarePartyFilter needs a dataOwner to be registered in the builder using a forDataOwner call"),
            null
        )
    }
}

fun FilterBuilder<HealthcareElement>.healthcareElementsByIds(vararg ids: String) {
    this.registerInParent { dataOwnerId -> HealthcareElementByIdsFilter(ids.toSet(), null) }
}

fun FilterBuilder<HealthcareElement>.healthcareElementsByIdentifiers(vararg identifiers: Identifier) {
    this.registerInParent { dataOwnerId ->
        HealthcareElementByHealthcarePartyIdentifiersFilter(
            null,
            dataOwnerId
                ?: throw IllegalArgumentException("DataSampleByHealthcarePartyAndIdentifiersFilter needs a dataOwner to be registered in the builder using a forDataOwner call"),
            identifiers.toList()
        )
    }
}

fun FilterBuilder<Patient>.allPatients() {
    this.registerInParent { dataOwnerId -> PatientByHealthcarePartyFilter(null, dataOwnerId) }
}

fun FilterBuilder<Patient>.patientsByIds(vararg ids: String) {
    this.registerInParent { dataOwnerId -> PatientByIdsFilter(null, ids.toList()) }
}

fun FilterBuilder<Patient>.patientsByIdentifiers(vararg identifiers: Identifier) {
    this.registerInParent { dataOwnerId ->
        PatientByHealthcarePartyAndIdentifiersFilter(
            null,
            dataOwnerId
                ?: throw IllegalArgumentException("PatientByHealthcarePartyAndIdentifiersFilter needs a dataOwner to be registered in the builder using a forDataOwner call"),
            identifiers.toList()
        )
    }
}

fun FilterBuilder<Patient>.byName(name: String) {
    this.registerInParent { dataOwnerId ->
        PatientByHealthcarePartyNameFilter(
            null,
            name,
            dataOwnerId
                ?: throw IllegalArgumentException("PatientByHealthcarePartyAndIdentifiersFilter needs a dataOwner to be registered in the builder using a forDataOwner call")
        )
    }
}

fun FilterBuilder<Patient>.byFuzzyName(name: String) {
    this.registerInParent { dataOwnerId ->
        PatientByHealthcarePartyNameContainsFuzzyFilter(
            null,
            name,
            dataOwnerId
                ?: throw IllegalArgumentException("PatientByHealthcarePartyAndIdentifiersFilter needs a dataOwner to be registered in the builder using a forDataOwner call")
        )
    }
}

fun FilterBuilder<Patient>.byGenderEducation(gender: Patient.Gender, education: String) =
    PatientByHealthcarePartyGenderEducationProfessionBuilder(
        gender,
        education
    ).also {
        this.registerInParent { dataOwnerId -> it.forDataOwner(dataOwnerId).build() }
    }

class PatientByHealthcarePartyGenderEducationProfessionBuilder(
    var gender: Patient.Gender? = null,
    var education: String? = null,
    var profession: String? = null
) : FilterBuilder<Patient>() {
    fun byProfession(profession: String) = this.also { it.profession = profession }
    override fun build(): PatientByHealthcarePartyGenderEducationProfession {
        return PatientByHealthcarePartyGenderEducationProfession(
            null,
            dataOwnerId
                ?: throw IllegalArgumentException("PatientByHealthcarePartyAndIdentifiersFilter needs a dataOwner to be registered in the builder using a forDataOwner call"),
            gender,
            education
        )
    }
}

fun FilterBuilder<Patient>.byDateOfBirth(after: LocalDateTime?, before: LocalDateTime?) {
    this.registerInParent { dataOwnerId ->
        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        PatientByHealthcarePartyDateOfBirthBetweenFilter(
            null,
            after?.format(formatter)?.toInt(),
            before?.format(formatter)?.toInt()
        )
    }
}

fun FilterBuilder<Patient>.byAge(age: Int, units: TemporalUnit = ChronoUnit.YEARS) {
    this.byDateOfBirth(
        LocalDateTime.now().minusYears(age.toLong() + 1).plusDays(1),
        LocalDateTime.now().minusYears(age.toLong())
    )
}

fun FilterBuilder<User>.allUsers() {
    this.registerInParent { dataOwnerId -> AllUsersFilter(null) }
}

fun FilterBuilder<User>.usersByIds(vararg ids: String) {
    this.registerInParent { dataOwnerId -> UserByIdsFilter(ids.toSet(), null) }
}

package io.icure.md.client.mappers

import io.icure.kraken.client.models.DeviceDto
import io.icure.md.client.models.MedicalDevice
import io.icure.md.client.models.SystemMetaDataOwner
import java.util.*

fun DeviceDto.toMedicalDevice() = MedicalDevice(
    id = this.id,
    identifiers = this.identifiers.map { it.toIdentifier() },
    labels = this.tags.map { it.toCodingReference() },
    codes = this.codes.map { it.toCodingReference() },
    properties = this.properties.map { it.toProperty() },
    rev = this.rev,
    deletionDate = this.deletionDate,
    created = this.created,
    modified = this.modified,
    author = this.author,
    responsible = this.responsible,
    endOfLife = this.endOfLife,
    externalId = this.externalId,
    name = this.name,
    type = this.type,
    brand = this.brand,
    model = this.model,
    serialNumber = this.serialNumber,
    parentId = this.parentId,
    picture = this.picture,
    systemMetaData = SystemMetaDataOwner(
        hcPartyKeys = this.hcPartyKeys,
        privateKeyShamirPartitions = this.privateKeyShamirPartitions,
        aesExchangeKeys = this.aesExchangeKeys,
        transferKeys = this.transferKeys,
        lostHcPartyKeys = this.lostHcPartyKeys
    )
)

fun MedicalDevice.toDeviceDto() = DeviceDto(
    id = this.id?.also {
        try {
            UUID.fromString(it)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Invalid id, id must be a valid UUID")
        }
    } ?: UUID.randomUUID().toString(),
    identifiers = this.identifiers.map { it.toIdentifierDto() },
    tags = this.labels.map { it.toCodeStubDto() },
    codes = this.codes.map { it.toCodeStubDto() },
    properties = this.properties.map { it.toPropertyStubDto() },
    rev = this.rev,
    deletionDate = this.deletionDate,
    created = this.created,
    modified = this.modified,
    author = this.author,
    responsible = this.responsible,
    endOfLife = this.endOfLife,
    externalId = this.externalId,
    name = this.name,
    type = this.type,
    brand = this.brand,
    model = this.model,
    serialNumber = this.serialNumber,
    parentId = this.parentId,
    picture = this.picture,
    hcPartyKeys = this.systemMetaData?.hcPartyKeys ?: emptyMap(),
    privateKeyShamirPartitions = this.systemMetaData?.privateKeyShamirPartitions ?: emptyMap(),
    aesExchangeKeys = this.systemMetaData?.aesExchangeKeys ?: emptyMap(),
    transferKeys = this.systemMetaData?.transferKeys ?: emptyMap(),
    lostHcPartyKeys = this.systemMetaData?.lostHcPartyKeys ?: emptyList()
)

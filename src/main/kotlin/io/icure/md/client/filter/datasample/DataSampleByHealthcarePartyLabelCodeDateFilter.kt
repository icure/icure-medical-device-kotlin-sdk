/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package io.icure.md.client.filter.datasample

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import io.icure.md.client.filter.Filter
import io.icure.md.client.models.DataSample

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class DataSampleByHealthcarePartyLabelCodeDateFilter(
    override val description: String? = null,
    val healthcarePartyId: String? = null,
    val patientSecretForeignKey: String? = null,
    val tagType: String? = null,
    val tagCode: String? = null,
    val codeType: String? = null,
    val codeCode: String? = null,
    val startValueDate: Long? = null,
    val endValueDate: Long? = null
) : Filter<DataSample>

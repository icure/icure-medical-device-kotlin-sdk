/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */
package io.icure.md.client.filter.patient

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import io.icure.kraken.client.models.PatientDto
import io.icure.md.client.filter.Filter

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
data class PatientByHealthcarePartyDateOfBirthBetweenFilter(
    override val description: String? = null,
    val minDateOfBirth: Int? = null,
    val maxDateOfBirth: Int? = null,
    val healthcarePartyId: String? = null
) : Filter<PatientDto>

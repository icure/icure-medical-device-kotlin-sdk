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
package io.icure.md.client.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.pozo.KotlinBuilder

/**
 *
 *
 * @param id the Id of the coding. We encourage using either a v4 UUID or a HL7 Id.
 * @param rev the revision of the coding in the database, used for conflict management / optimistic locking.
 * @param type
 * @param code
 * @param version Must be lexicographically searchable
 * @param description Description (ex: {en: Rheumatic Aortic Stenosis, fr: Sténose rhumatoïde de l'Aorte})
 * @param qualifiedLinks Links towards related codes
 * @param searchTerms Extra search terms/ language
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class Coding(

    /* the Id of the coding. We encourage using either a v4 UUID or a HL7 Id. */
    @field:JsonProperty("id")
    val id: kotlin.String? = null,

    /* the revision of the coding in the database, used for conflict management / optimistic locking. */
    @field:JsonProperty("rev")
    val rev: kotlin.String? = null,

    @field:JsonProperty("type")
    val type: kotlin.String? = null,

    @field:JsonProperty("code")
    val code: kotlin.String? = null,

    /* Must be lexicographically searchable */
    @field:JsonProperty("version")
    val version: kotlin.String? = null,

    /* Description (ex: {en: Rheumatic Aortic Stenosis, fr: Sténose rhumatoïde de l'Aorte}) */
    @field:JsonProperty("description")
    val description: kotlin.collections.Map<kotlin.String, kotlin.String>? = null,

    /* Links towards related codes */
    @field:JsonProperty("qualifiedLinks")
    val qualifiedLinks: kotlin.collections.Map<kotlin.String, kotlin.collections.List<kotlin.String>> = emptyMap(),

    /* Extra search terms/ language */
    @field:JsonProperty("searchTerms")
    val searchTerms: kotlin.collections.Map<kotlin.String, kotlin.collections.Set<kotlin.String>> = emptyMap()

)

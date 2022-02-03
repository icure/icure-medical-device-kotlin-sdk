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
 * @param id The Id of the Data sample. We encourage using either a v4 UUID or a HL7 Id.
 * @param transactionId The transactionId is used when a single data sample had to be split into parts for technical reasons. Several data samples with the same non null transaction id form one single data sample
 * @param identifier 
 * @param batchId Id of the batch that embeds this data sample
 * @param healthElementsIds List of IDs of all healthcare elements for which the data sample is provided. Only used when the Data sample is emitted outside of its contact
 * @param canvasesIds List of Ids of all canvases linked to the Data sample. Only used when the Data sample is emitted outside of its contact.
 * @param index Used for sorting data samples inside an upper object (A contact, a transaction, a FHIR bundle, ...)
 * @param content Information contained in the data sample. Content is localized, using ISO language code as key
 * @param valueDate The date (YYYYMMDDhhmmss) when the Data sample is noted to have started and also closes on the same date
 * @param openingDate The date (YYYYMMDDhhmmss) of the start of the Data sample
 * @param closingDate The date (YYYYMMDDhhmmss) marking the end of the Data sample
 * @param created 
 * @param modified 
 * @param endOfLife 
 * @param author 
 * @param responsible 
 * @param comment Text, comments on the Data sample provided
 * @param qualifiedLinks Links towards related data samples (possibly in other contacts)
 * @param codes 
 * @param labels 
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class DataSample (

    /* The Id of the Data sample. We encourage using either a v4 UUID or a HL7 Id. */
    @field:JsonProperty("id")
    val id: kotlin.String? = null,

    /* The transactionId is used when a single data sample had to be split into parts for technical reasons. Several data samples with the same non null transaction id form one single data sample */
    @field:JsonProperty("transactionId")
    val transactionId: kotlin.String? = null,

    @field:JsonProperty("identifier")
    val identifier: kotlin.collections.List<Identifier> = emptyList(),

    /* Id of the batch that embeds this data sample */
    @field:JsonProperty("batchId")
    val batchId: kotlin.String? = null,

    /* List of IDs of all healthcare elements for which the data sample is provided. Only used when the Data sample is emitted outside of its contact */
    @field:JsonProperty("healthElementsIds")
    val healthElementsIds: kotlin.collections.Set<kotlin.String>? = null,

    /* List of Ids of all canvases linked to the Data sample. Only used when the Data sample is emitted outside of its contact. */
    @field:JsonProperty("canvasesIds")
    val canvasesIds: kotlin.collections.Set<kotlin.String>? = null,

    /* Used for sorting data samples inside an upper object (A contact, a transaction, a FHIR bundle, ...) */
    @field:JsonProperty("index")
    val index: kotlin.Long? = null,

    /* Information contained in the data sample. Content is localized, using ISO language code as key */
    @field:JsonProperty("content")
    val content: kotlin.collections.Map<kotlin.String, Content> = emptyMap(),

    /* The date (YYYYMMDDhhmmss) when the Data sample is noted to have started and also closes on the same date */
    @field:JsonProperty("valueDate")
    val valueDate: kotlin.Long? = null,

    /* The date (YYYYMMDDhhmmss) of the start of the Data sample */
    @field:JsonProperty("openingDate")
    val openingDate: kotlin.Long? = null,

    /* The date (YYYYMMDDhhmmss) marking the end of the Data sample */
    @field:JsonProperty("closingDate")
    val closingDate: kotlin.Long? = null,

    @field:JsonProperty("created")
    val created: kotlin.Long? = null,

    @field:JsonProperty("modified")
    val modified: kotlin.Long? = null,

    @field:JsonProperty("endOfLife")
    val endOfLife: kotlin.Long? = null,

    @field:JsonProperty("author")
    val author: kotlin.String? = null,

    @field:JsonProperty("responsible")
    val responsible: kotlin.String? = null,

    /* Text, comments on the Data sample provided */
    @field:JsonProperty("comment")
    val comment: kotlin.String? = null,

    /* Links towards related data samples (possibly in other contacts) */
    @field:JsonProperty("qualifiedLinks")
    val qualifiedLinks: kotlin.collections.Map<kotlin.String, kotlin.collections.Map<kotlin.String, kotlin.String>> = emptyMap(),

    @field:JsonProperty("codes")
    val codes: kotlin.collections.List<CodingReference> = emptyList(),

    @field:JsonProperty("labels")
    val labels: kotlin.collections.List<CodingReference> = emptyList()

)


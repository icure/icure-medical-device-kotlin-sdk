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
 * @param identifier 
 * @param content Information contained in the data sample. Content is localized, using ISO language code as key
 * @param qualifiedLinks Links towards related data samples (possibly in other contacts)
 * @param codes 
 * @param labels 
 * @param transactionId The transactionId is used when a single data sample had to be split into parts for technical reasons. Several data samples with the same non null transaction id form one single data sample
 * @param batchId Id of the batch that embeds this data sample
 * @param healthElementsIds List of IDs of all healthcare elements for which the data sample is provided. Only used when the Data sample is emitted outside of its contact
 * @param canvasesIds List of Ids of all canvases linked to the Data sample. Only used when the Data sample is emitted outside of its contact.
 * @param index Used for sorting data samples inside an upper object (A contact, a transaction, a FHIR bundle, ...)
 * @param valueDate The date (YYYYMMDDhhmmss) when the Data sample is noted to have started and also closes on the same date
 * @param openingDate The date (YYYYMMDDhhmmss) of the start of the Data sample
 * @param closingDate The date (YYYYMMDDhhmmss) marking the end of the Data sample
 * @param created 
 * @param modified 
 * @param endOfLife 
 * @param author 
 * @param responsible 
 * @param comment Text, comments on the Data sample provided
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class DataSample (

    /* The Id of the Data sample. We encourage using either a v4 UUID or a HL7 Id. */
    @field:JsonProperty("id")
    val id: String? = null,

    @field:JsonProperty("identifier")
    val identifier: List<Identifier> = emptyList(),

    /* Information contained in the data sample. Content is localized, using ISO language code as key */
    @field:JsonProperty("content")
    val content: Map<String, Content> = emptyMap(),

    /* Links towards related data samples (possibly in other contacts) */
    @field:JsonProperty("qualifiedLinks")
    val qualifiedLinks: Map<String, Map<String, String>> = emptyMap(),

    @field:JsonProperty("codes")
    val codes: List<CodingReference> = emptyList(),

    @field:JsonProperty("labels")
    val labels: List<CodingReference> = emptyList(),

    /* The transactionId is used when a single data sample had to be split into parts for technical reasons. Several data samples with the same non null transaction id form one single data sample */
    @field:JsonProperty("transactionId")
    val transactionId: String? = null,

    /* Id of the batch that embeds this data sample */
    @field:JsonProperty("batchId")
    val batchId: String? = null,

    /* List of IDs of all healthcare elements for which the data sample is provided. Only used when the Data sample is emitted outside of its contact */
    @field:JsonProperty("healthElementsIds")
    val healthElementsIds: Set<String>? = null,

    /* List of Ids of all canvases linked to the Data sample. Only used when the Data sample is emitted outside of its contact. */
    @field:JsonProperty("canvasesIds")
    val canvasesIds: Set<String>? = null,

    /* Used for sorting data samples inside an upper object (A contact, a transaction, a FHIR bundle, ...) */
    @field:JsonProperty("index")
    val index: Long? = null,

    /* The date (YYYYMMDDhhmmss) when the Data sample is noted to have started and also closes on the same date */
    @field:JsonProperty("valueDate")
    val valueDate: Long? = null,

    /* The date (YYYYMMDDhhmmss) of the start of the Data sample */
    @field:JsonProperty("openingDate")
    val openingDate: Long? = null,

    /* The date (YYYYMMDDhhmmss) marking the end of the Data sample */
    @field:JsonProperty("closingDate")
    val closingDate: Long? = null,

    @field:JsonProperty("created")
    val created: Long? = null,

    @field:JsonProperty("modified")
    val modified: Long? = null,

    @field:JsonProperty("endOfLife")
    val endOfLife: Long? = null,

    @field:JsonProperty("author")
    val author: String? = null,

    @field:JsonProperty("responsible")
    val responsible: String? = null,

    /* Text, comments on the Data sample provided */
    @field:JsonProperty("comment")
    val comment: String? = null

)


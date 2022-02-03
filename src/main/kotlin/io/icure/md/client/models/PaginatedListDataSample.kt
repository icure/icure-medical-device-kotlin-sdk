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
 * @param pageSize 
 * @param totalSize 
 * @param rows 
 * @param nextKeyPair 
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class PaginatedListDataSample (

    @field:JsonProperty("pageSize")
    val pageSize: Int,

    @field:JsonProperty("totalSize")
    val totalSize: Int,

    @field:JsonProperty("rows")
    val rows: List<DataSample> = emptyList(),

    @field:JsonProperty("nextKeyPair")
    val nextKeyPair: PaginatedDocumentKeyAndIdPairObject? = null

)


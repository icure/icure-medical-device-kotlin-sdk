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
 * @param tags
 * @param owner
 * @param delegatedTo
 * @param key
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class Delegation(

    @field:JsonProperty("tags")
    val tags: kotlin.collections.List<kotlin.String> = emptyList(),

    @field:JsonProperty("owner")
    val owner: kotlin.String? = null,

    @field:JsonProperty("delegatedTo")
    val delegatedTo: kotlin.String? = null,

    @field:JsonProperty("key")
    val key: kotlin.String? = null

)

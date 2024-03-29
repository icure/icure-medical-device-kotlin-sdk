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
 * Typically used for business / client identifiers. An identifier should identify a patient uniquely and unambiguously. However, iCure can't guarantee the uniqueness of those identifiers : This is something you need to take care of.
 *
 * @param id
 * @param assigner
 * @param start
 * @param end
 * @param system
 * @param type
 * @param use
 * @param `value`
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class Identifier(

    @field:JsonProperty("id")
    val id: kotlin.String? = null,

    @field:JsonProperty("assigner")
    val assigner: kotlin.String? = null,

    @field:JsonProperty("start")
    val start: kotlin.String? = null,

    @field:JsonProperty("end")
    val end: kotlin.String? = null,

    @field:JsonProperty("system")
    val system: kotlin.String? = null,

    @field:JsonProperty("type")
    val type: CodingReference? = null,

    @field:JsonProperty("use")
    val use: kotlin.String? = null,

    @field:JsonProperty("value")
    val `value`: kotlin.String? = null

)

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
 * @param identifier 
 * @param type 
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@KotlinBuilder
data class PropertyType (

    @field:JsonProperty("identifier")
    val identifier: kotlin.String? = null,

    @field:JsonProperty("type")
    val type: PropertyType.Type? = null

) {

    /**
     * 
     *
     * Values: bOOLEAN,iNTEGER,dOUBLE,sTRING,dATE,cLOB,jSON
     */
    enum class Type(val value: kotlin.String) {
        @JsonProperty(value = "BOOLEAN") bOOLEAN("BOOLEAN"),
        @JsonProperty(value = "INTEGER") iNTEGER("INTEGER"),
        @JsonProperty(value = "DOUBLE") dOUBLE("DOUBLE"),
        @JsonProperty(value = "STRING") sTRING("STRING"),
        @JsonProperty(value = "DATE") dATE("DATE"),
        @JsonProperty(value = "CLOB") cLOB("CLOB"),
        @JsonProperty(value = "JSON") jSON("JSON");
    }
}


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
package io.icure.md.client.apis

import io.icure.kraken.client.infrastructure.ClientException
import io.icure.kraken.client.infrastructure.ServerException
import io.icure.md.client.models.Filter
import io.icure.md.client.models.HealthcareElement
import io.icure.md.client.models.PaginatedListHealthcareElement
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Named

@Named
@ExperimentalStdlibApi
@ExperimentalCoroutinesApi
interface HealthcareElementApi {

    /**
     * Create a Healthcare Element
     *
     * @param healthcareElement
     * @return HealthcareElement
     * @throws UnsupportedOperationException If the API returns an informational or redirection response
     * @throws ClientException If the API returns a client error response
     * @throws ServerException If the API returns a server error response
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(UnsupportedOperationException::class, ClientException::class, ServerException::class)
    suspend fun createOrModifyHealthcareElement(healthcareElement: HealthcareElement): HealthcareElement

    /**
     * Create a Healthcare Element
     *
     * @param healthcareElement
     * @return kotlin.collections.List<HealthcareElement>
     * @throws UnsupportedOperationException If the API returns an informational or redirection response
     * @throws ClientException If the API returns a client error response
     * @throws ServerException If the API returns a server error response
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(UnsupportedOperationException::class, ClientException::class, ServerException::class)
    suspend fun createOrModifyHealthcareElements(healthcareElement: kotlin.collections.List<HealthcareElement>): kotlin.collections.List<HealthcareElement>

    /**
     * Delete a Healthcare Element
     *
     * @param id
     * @return kotlin.String
     * @throws UnsupportedOperationException If the API returns an informational or redirection response
     * @throws ClientException If the API returns a client error response
     * @throws ServerException If the API returns a server error response
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(UnsupportedOperationException::class, ClientException::class, ServerException::class)
    suspend fun deleteHealthcareElement(id: kotlin.String): kotlin.String

    /**
     * Find Healthcare Elements using a filter
     *
     * @param filter
     * @return PaginatedListHealthcareElement
     * @throws UnsupportedOperationException If the API returns an informational or redirection response
     * @throws ClientException If the API returns a client error response
     * @throws ServerException If the API returns a server error response
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(UnsupportedOperationException::class, ClientException::class, ServerException::class)
    suspend fun filterHealthcareElement(filter: Filter): PaginatedListHealthcareElement

    /**
     * Get a Healthcare Element
     *
     * @param id
     * @return HealthcareElement
     * @throws UnsupportedOperationException If the API returns an informational or redirection response
     * @throws ClientException If the API returns a client error response
     * @throws ServerException If the API returns a server error response
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(UnsupportedOperationException::class, ClientException::class, ServerException::class)
    suspend fun getHealthcareElement(id: kotlin.String): HealthcareElement

    /**
     * Find Healthcare Elements using a filter
     *
     * @param filter
     * @return kotlin.collections.List<kotlin.String>
     * @throws UnsupportedOperationException If the API returns an informational or redirection response
     * @throws ClientException If the API returns a client error response
     * @throws ServerException If the API returns a server error response
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(UnsupportedOperationException::class, ClientException::class, ServerException::class)
    suspend fun matchHealthcareElement(filter: Filter): kotlin.collections.List<kotlin.String>

}

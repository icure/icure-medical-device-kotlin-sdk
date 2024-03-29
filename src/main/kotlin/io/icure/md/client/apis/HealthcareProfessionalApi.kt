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
import io.icure.md.client.filter.Filter
import io.icure.md.client.models.HealthcareProfessional
import io.icure.md.client.models.PaginatedListHealthcareProfessional
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Named

@Named
@ExperimentalStdlibApi
@ExperimentalCoroutinesApi
interface HealthcareProfessionalApi {

    /**
     * Create a new healthcare professional or modify an existing one.
     * When modifying an healthcare professional, you must ensure that the rev obtained when getting or creating the healthcare professional is present as the rev is used to guarantee that the healthcare professional has not been modified by a third party.
     * @param healthcareProfessional The healthcare professional that must be created in the database.
     * @return Returns the created or modified healthcare professional as a Healthcare professional object, with an updated rev.
     * @throws ClientException if you make this call without providing an authentication token (BASIC, SessionId).
     * @throws UnsupportedOperationException If the API returns an informational or redirection response
     * @throws ServerException If the API returns a server error response
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(UnsupportedOperationException::class, ClientException::class, ServerException::class)
    suspend fun createOrModifyHealthcareProfessional(healthcareProfessional: HealthcareProfessional): HealthcareProfessional

    /**
     * Delete an existing healthcare professional.
     * Deletes the healthcare professional identified by the provided unique hcpId.
     * @param hcpId The UUID that uniquely identifies the healthcare professional to be deleted.
     * @return Returns the rev of the deleted object.
     * @throws ClientException if you make this call without providing an authentication token (BASIC, SessionId).
     * @throws ClientException if there is no healthcare professional with the provided hcpId.
     * @throws UnsupportedOperationException If the API returns an informational or redirection response
     * @throws ServerException If the API returns a server error response
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(UnsupportedOperationException::class, ClientException::class, ServerException::class)
    suspend fun deleteHealthcareProfessional(hcpId: kotlin.String): kotlin.String

    /**
     * Load healthcare professionals from the database by filtering them using the provided Filter.
     * Filters are complex selectors that are built by combining basic building blocks. Examples of filters available for Healthcare professionals are AllHealthcareProfessionalsFilter and HealthcarProfessionalsByIdsFilter. This method returns a paginated list of healthcare professionals (with a cursor that lets you query the following items).
     * @param filter The Filter object that describes which condition(s) the elements whose the ids should be returned must fulfill
     * @param nextHcpId The id of the first Healthcare professional in the next page (optional)
     * @param limit The number of healthcare professionals to return in the queried page (optional)
     * @return Returns a PaginatedList of Healthcare professionals.
     * @throws ClientException if you make this call without providing an authentication token (BASIC, SessionId).
     * @throws UnsupportedOperationException If the API returns an informational or redirection response
     * @throws ServerException If the API returns a server error response
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(UnsupportedOperationException::class, ClientException::class, ServerException::class)
    suspend fun filterHealthcareProfessionalBy(filter: Filter<HealthcareProfessional>, nextHcpId: kotlin.String?, limit: kotlin.Int?): PaginatedListHealthcareProfessional

    /**
     * Get a Healthcare professional by id.
     * Each healthcare professional is uniquely identified by a healthcare professional id. The healthcare professional id is a UUID. This hcpId is the preferred method to retrieve one specific healthcare professional.
     * @param hcpId The UUID that identifies the healthcare professional uniquely
     * @return Returns the fetched healthcare professional as a Healthcare professional object
     * @throws ClientException if you make this call without providing an authentication token (BASIC, SessionId).
     * @throws ClientException if there is no healthcare professional with the provided hcpId.
     * @throws UnsupportedOperationException If the API returns an informational or redirection response
     * @throws ServerException If the API returns a server error response
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(UnsupportedOperationException::class, ClientException::class, ServerException::class)
    suspend fun getHealthcareProfessional(hcpId: kotlin.String): HealthcareProfessional

    /**
     * Load healthcare professional ids from the database by filtering them using the provided Filter.
     * Filters are complex selectors that are built by combining basic building blocks. Examples of filters available for Healthcare professionals are AllHealthcare professionalsFilter and Healthcare professionalsByIdsFilter. This method returns the list of the ids of the healthcare professionals matching the filter.
     * @param filter The Filter object that describes which condition(s) the elements whose the ids should be returned must fulfill
     * @return Returns a list of all healthcare professional ids matching the filter.
     * @throws ClientException if you make this call without providing an authentication token (BASIC, SessionId).
     * @throws UnsupportedOperationException If the API returns an informational or redirection response
     * @throws ServerException If the API returns a server error response
     */
    @Suppress("UNCHECKED_CAST")
    @Throws(UnsupportedOperationException::class, ClientException::class, ServerException::class)
    suspend fun matchHealthcareProfessionalBy(filter: Filter<HealthcareProfessional>): kotlin.collections.List<kotlin.String>
}

package io.icure.md.client.apis.e2e

import io.icure.kraken.client.crypto.CryptoUtils
import io.icure.kraken.client.crypto.privateKeyAsString
import io.icure.kraken.client.crypto.publicKeyAsString
import io.icure.md.client.apis.AnonymousMedTechApi
import io.icure.md.client.apis.infrastructure.MailUtils
import io.icure.md.client.mappers.dataOwnerId
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.*
import kotlin.time.ExperimentalTime

@FlowPreview
@ExperimentalStdlibApi
@ExperimentalCoroutinesApi
@ExperimentalTime
@ExperimentalUnsignedTypes
@DisplayName("User tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class UserApiImplTest {

//    private val iCurePath = "https://kraken.icure.dev"
//    private val authHeader = TestUtils.basicAuthFrom(".credentials")
//    private val healthcareProfessionalId = "782f1bcd-9f3f-408a-af1b-cd9f3f908a98"
//    private val healthcareProfessionalPrivateKey =
//        TestUtils.healthcareProfessionalPrivateKey(healthcareProfessionalId, this::class.java)
//    private val healthcareProfessionalPublicKey =
//        runBlocking { TestUtils.healthcareProfessionalPublicKey(iCurePath, authHeader, healthcareProfessionalId) }

//    private val medTechApi = MedTechApi.Builder()
//        .iCureUrlPath(iCurePath)
//        .authorization(authHeader)
//        .addKeyPair(healthcareProfessionalId, healthcareProfessionalPublicKey, healthcareProfessionalPrivateKey)
//        .build()

    // private val anonymousMedTechApi = AnonymousMedTechApi.Builder().authProcessId(authProcessId = "f0ced6c6-d7cb-4f78-841e-2674ad09621e").iCureUrlPath("http://127.0.0.1:16043").build()
    private val anonymousMedTechApi =
        AnonymousMedTechApi.Builder().authProcessId(authProcessId = "6a355458dbfa392cb56244031907f47a")
            .iCureUrlPath("http://127.0.0.1:16043").build()

    @Test
    fun selfCreatedUser() {
        runBlocking {
            val emailAddress = MailUtils.getEmailAddress()
            val userName = UUID.randomUUID().toString().take(8)
            val process = anonymousMedTechApi.authenticationApi.startAuthentication(
                "171f186a-7a2a-40f0-b842-b486428c771b",
                userName,
                "",
                emailAddress,
                "a58afe0e-02dc-431b-8155-0351140099e4"
            )

            println("UserName: $userName")

            assert(process != null)
            println("ProcessId: ${process!!.processId}")
            println("Login: ${process.login}")

            delay(10000)
            val email = MailUtils.readInbox(emailAddress).firstOrNull()
            assert(email != null)

            val validationCode = email!!.mail_subject.takeLast(6)
            val keyPair = CryptoUtils.generateKeyPairRSA()
            val tokenAndKeyProvider: ((String, String) -> Triple<String, String, String>?) =
                { _: String, _: String ->
                    null
                }
            val result = anonymousMedTechApi.authenticationApi.completeAuthentication(
                process,
                validationCode,
                keyPair,
                tokenAndKeyProvider
            )

            val api = result.medTechApi
            val currentUser = api.userApi().getLoggedUser()

            currentUser.patientId?.let { patientId ->
                val patient = api.patientApi().getPatient(patientId)
                assert(patient != null)
            }

            TestUtils.writeUserCredentials(
                TestUtils.UserCredentials(
                    emailAddress,
                    result.token,
                    currentUser.dataOwnerId(),
                    result.keyPair.publicKeyAsString(),
                    result.keyPair.privateKeyAsString()
                ),
                currentUser.patientId?.let { "pat_" } ?: currentUser.healthcarePartyId?.let { "hcp_" }
                ?: currentUser.deviceId?.let { "dev_" } ?: "s"
            )

        }
    }

}


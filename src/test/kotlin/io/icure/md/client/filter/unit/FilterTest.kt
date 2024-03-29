package io.icure.md.client.filter.unit

import io.icure.md.client.filter.UnionFilter
import io.icure.md.client.filter.byAge
import io.icure.md.client.filter.byGenderEducation
import io.icure.md.client.filter.byIdentifiers
import io.icure.md.client.filter.byName
import io.icure.md.client.filter.filter
import io.icure.md.client.filter.intersection
import io.icure.md.client.filter.patient.PatientByHealthcarePartyAndIdentifiersFilter
import io.icure.md.client.filter.union
import io.icure.md.client.models.Identifier
import io.icure.md.client.models.Patient
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@ExperimentalStdlibApi
@ExperimentalCoroutinesApi
@DisplayName("Filter tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FilterTest {
    @Test
    fun dslPatientTest() {
        val filter = filter<Patient> {
            forDataOwner("123")
            union {
                byIdentifiers(Identifier("pat-1"), Identifier("pat-2"))
                intersection {
                    byName("Churchill")
                    byGenderEducation(Patient.Gender.male, "college").byProfession("lawyer")
                    byAge(30)
                }
            }
        }.build()

        Assertions.assertEquals(UnionFilter::class, filter::class, "Generated filter should be UnionFilter")
        Assertions.assertEquals(2, (filter as UnionFilter).filters.size, "Union filter should have 2 children")
        Assertions.assertEquals(
            PatientByHealthcarePartyAndIdentifiersFilter::class,
            filter.filters[0]::class,
            "First filter in union must be of class PatientByHealthcarePartyAndIdentifiersFilter"
        )
        Assertions.assertEquals(
            "123",
            (filter.filters[0] as PatientByHealthcarePartyAndIdentifiersFilter).healthcarePartyId,
            "Hcp must be set in filters"
        )
    }

    @Test
    fun dslForHcpOrderTest() {
        val filter1 = filter<Patient> {
            union {
                byIdentifiers(Identifier("pat-1"), Identifier("pat-2"))
                intersection {
                    byName("Churchill")
                    byGenderEducation(Patient.Gender.male, "college")
                }
            }
            forDataOwner("123")
        }.build()

        val filter2 = filter<Patient> {
            forDataOwner("123")
            union {
                byIdentifiers(Identifier("pat-1"), Identifier("pat-2"))
                intersection {
                    byName("Churchill")
                    byGenderEducation(Patient.Gender.male, "college")
                }
            }
        }.build()

        Assertions.assertEquals(filter1, filter2, "Generated filters should be the same")
    }
}

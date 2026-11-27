package dev.jordond.compass.geocoder.mobile.internal

import android.location.Address
import android.location.Geocoder
import dev.jordond.compass.geocoder.exception.GeocodeException
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class OperationTest {

    @Test
    fun syncOperationReturnsTheAddresses() = runTest {
        val address = mockk<Address>()

        assertEquals(listOf(address), syncOperation { mutableListOf(address) })
    }

    @Test
    fun syncOperationTurnsANullResultIntoAnEmptyList() = runTest {
        assertEquals(emptyList(), syncOperation { null })
    }

    @Test
    fun syncOperationWrapsFailuresInAGeocodeException() = runTest {
        val error = assertFailsWith<GeocodeException> {
            syncOperation { throw java.io.IOException("service unavailable") }
        }

        assertContains(error.message.orEmpty(), "service unavailable")
    }

    @Test
    fun asyncOperationResumesWithACopyOfTheAddresses() = runTest {
        val address = mockk<Address>()
        val source = mutableListOf(address)

        val result = asyncOperation { listener -> listener.onGeocode(source) }
        source.clear()

        assertEquals(1, result.size)
        assertSame(address, result.single())
    }

    @Test
    fun asyncOperationFailsWithAGeocodeExceptionOnError() = runTest {
        val error = assertFailsWith<GeocodeException> {
            asyncOperation { listener: Geocoder.GeocodeListener -> listener.onError("no network") }
        }

        assertContains(error.message.orEmpty(), "no network")
    }
}

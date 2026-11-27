package dev.jordond.compass.geocoder.internal

import dev.jordond.compass.Coordinates
import dev.jordond.compass.exception.NotFoundException
import dev.jordond.compass.exception.NotSupportedException
import dev.jordond.compass.geocoder.FakePlatformGeocoder
import dev.jordond.compass.geocoder.Geocoder
import dev.jordond.compass.geocoder.GeocoderResult
import dev.jordond.compass.geocoder.place
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertSame
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultGeocoderTest {

    private fun TestScopeGeocoder(fake: FakePlatformGeocoder, scope: kotlinx.coroutines.test.TestScope) =
        DefaultGeocoder(fake, StandardTestDispatcher(scope.testScheduler))

    @Test
    fun isAvailableDelegatesToPlatform() = runTest {
        val fake = FakePlatformGeocoder(available = true)
        val geocoder = TestScopeGeocoder(fake, this)
        assertTrue(geocoder.isAvailable())
        fake.available = false
        assertFalse(geocoder.isAvailable())
    }

    @Test
    fun platformGeocoderIsExposed() = runTest {
        val fake = FakePlatformGeocoder()
        assertSame(fake, TestScopeGeocoder(fake, this).platformGeocoder)
    }

    @Test
    fun forwardReturnsSuccessWithData() = runTest {
        val coordinates = listOf(Coordinates(1.0, 2.0), Coordinates(3.0, 4.0))
        val fake = FakePlatformGeocoder(onForward = { coordinates })

        val result = TestScopeGeocoder(fake, this).forward("somewhere")

        assertIs<GeocoderResult.Success<Coordinates>>(result)
        assertEquals(coordinates, result.data)
        assertEquals("somewhere", fake.lastAddress)
    }

    @Test
    fun reverseReturnsSuccessWithData() = runTest {
        val places = listOf(place("A"), place("B"))
        val fake = FakePlatformGeocoder(onReverse = { _, _ -> places })

        val result = TestScopeGeocoder(fake, this).reverse(10.5, -20.5)

        assertIs<GeocoderResult.Success<*>>(result)
        assertEquals(places, result.data)
        assertEquals(10.5 to -20.5, fake.lastReverse)
    }

    @Test
    fun reverseWithCoordinatesPassesLatitudeAndLongitude() = runTest {
        val fake = FakePlatformGeocoder(onReverse = { _, _ -> listOf(place()) })

        TestScopeGeocoder(fake, this).reverse(Coordinates(5.0, 6.0))

        assertEquals(5.0 to 6.0, fake.lastReverse)
    }

    @Test
    fun placesAliasesReverse() = runTest {
        val fake = FakePlatformGeocoder(onReverse = { _, _ -> listOf(place()) })
        val geocoder = TestScopeGeocoder(fake, this)

        assertIs<GeocoderResult.Success<*>>(geocoder.places(1.0, 2.0))
        assertIs<GeocoderResult.Success<*>>(geocoder.places(Coordinates(1.0, 2.0)))
        assertEquals(2, fake.reverseCalls)
    }

    @Test
    fun locationsAliasesForward() = runTest {
        val fake = FakePlatformGeocoder(onForward = { listOf(Coordinates(1.0, 1.0)) })

        assertIs<GeocoderResult.Success<Coordinates>>(TestScopeGeocoder(fake, this).locations("x"))
        assertEquals(1, fake.forwardCalls)
    }

    @Test
    fun emptyListMapsToNotFound() = runTest {
        val geocoder = TestScopeGeocoder(FakePlatformGeocoder(), this)

        assertSame(GeocoderResult.NotFound, geocoder.forward("x"))
        assertSame(GeocoderResult.NotFound, geocoder.reverse(1.0, 1.0))
    }

    @Test
    fun unavailablePlatformMapsToNotSupportedWithoutCallingPlatform() = runTest {
        val fake = FakePlatformGeocoder(available = false)
        val geocoder = TestScopeGeocoder(fake, this)

        assertSame(GeocoderResult.NotSupported, geocoder.forward("x"))
        assertSame(GeocoderResult.NotSupported, geocoder.reverse(1.0, 1.0))
        assertEquals(0, fake.forwardCalls)
        assertEquals(0, fake.reverseCalls)
    }

    @Test
    fun notSupportedExceptionMapsToNotSupported() = runTest {
        val fake = FakePlatformGeocoder(onForward = { throw NotSupportedException() })
        assertSame(GeocoderResult.NotSupported, TestScopeGeocoder(fake, this).forward("x"))
    }

    @Test
    fun illegalArgumentExceptionMapsToInvalidCoordinates() = runTest {
        val fake = FakePlatformGeocoder(onReverse = { _, _ -> throw IllegalArgumentException("bad") })
        assertSame(GeocoderResult.InvalidCoordinates, TestScopeGeocoder(fake, this).reverse(999.0, 999.0))
    }

    @Test
    fun notFoundExceptionMapsToNotFound() = runTest {
        val fake = FakePlatformGeocoder(onForward = { throw NotFoundException() })
        assertSame(GeocoderResult.NotFound, TestScopeGeocoder(fake, this).forward("x"))
    }

    @Test
    fun otherThrowableMapsToGeocodeFailedWithMessage() = runTest {
        val fake = FakePlatformGeocoder(onForward = { throw IllegalStateException("boom") })

        val result = TestScopeGeocoder(fake, this).forward("x")

        assertIs<GeocoderResult.GeocodeFailed>(result)
        assertEquals("boom", result.message)
    }

    @Test
    fun throwableWithoutMessageMapsToUnknownError() = runTest {
        val fake = FakePlatformGeocoder(onReverse = { _, _ -> throw RuntimeException() })

        val result = TestScopeGeocoder(fake, this).reverse(1.0, 1.0)

        assertIs<GeocoderResult.GeocodeFailed>(result)
        assertEquals("Unknown error", result.message)
    }

    @Test
    fun cancellationExceptionIsRethrown() = runTest {
        val fake = FakePlatformGeocoder(onForward = { throw CancellationException("cancelled") })
        assertFailsWith<CancellationException> { TestScopeGeocoder(fake, this).forward("x") }
    }

    @Test
    fun factoryFunctionsCreateWorkingGeocoders() = runTest {
        val fake = FakePlatformGeocoder(
            onForward = { listOf(Coordinates(1.0, 2.0)) },
            onReverse = { _, _ -> listOf(place()) },
        )
        val dispatcher = StandardTestDispatcher(testScheduler)

        assertIs<GeocoderResult.Success<*>>(Geocoder(fake, dispatcher).forward("x"))
        assertIs<GeocoderResult.Success<*>>(dev.jordond.compass.geocoder.ForwardGeocoder(fake, dispatcher).forward("x"))
        assertIs<GeocoderResult.Success<*>>(dev.jordond.compass.geocoder.ReverseGeocoder(fake, dispatcher).reverse(1.0, 2.0))
    }
}

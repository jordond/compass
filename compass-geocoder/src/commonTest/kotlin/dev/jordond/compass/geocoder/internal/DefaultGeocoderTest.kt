package dev.jordond.compass.geocoder.internal

import dev.jordond.compass.Coordinates
import dev.jordond.compass.exception.NotFoundException
import dev.jordond.compass.exception.NotSupportedException
import dev.jordond.compass.geocoder.FakePlatformGeocoder
import dev.jordond.compass.geocoder.ForwardGeocoder
import dev.jordond.compass.geocoder.Geocoder
import dev.jordond.compass.geocoder.GeocoderResult
import dev.jordond.compass.geocoder.ReverseGeocoder
import dev.jordond.compass.geocoder.geocoder
import dev.jordond.compass.test.place
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertSame
import kotlin.test.assertTrue

class DefaultGeocoderTest {

    @Test
    fun isAvailableDelegatesToPlatform() = runTest {
        val fake = FakePlatformGeocoder(available = true)
        val geocoder = geocoder(fake)

        assertTrue(geocoder.isAvailable())
        fake.available = false
        assertFalse(geocoder.isAvailable())
    }

    @Test
    fun forwardReturnsSuccessWithData() = runTest {
        val coordinates = listOf(Coordinates(1.0, 2.0), Coordinates(3.0, 4.0))
        val fake = FakePlatformGeocoder(onForward = { coordinates })

        val result = geocoder(fake).forward("somewhere")

        assertEquals(GeocoderResult.Success(coordinates), result)
        assertEquals("somewhere", fake.lastAddress)
    }

    @Test
    fun reverseReturnsSuccessWithData() = runTest {
        val places = listOf(place(name = "A"), place(name = "B"))
        val fake = FakePlatformGeocoder(onReverse = { _, _ -> places })

        val result = geocoder(fake).reverse(10.5, -20.5)

        assertEquals(GeocoderResult.Success(places), result)
        assertEquals(10.5 to -20.5, fake.lastReverse)
    }

    @Test
    fun reverseWithCoordinatesPassesLatitudeAndLongitude() = runTest {
        val fake = FakePlatformGeocoder(onReverse = { _, _ -> listOf(place()) })

        geocoder(fake).reverse(Coordinates(5.0, 6.0))

        assertEquals(5.0 to 6.0, fake.lastReverse)
    }

    @Test
    fun placesAliasesReverse() = runTest {
        val fake = FakePlatformGeocoder(onReverse = { _, _ -> listOf(place()) })
        val geocoder = geocoder(fake)

        assertIs<GeocoderResult.Success<*>>(geocoder.places(1.0, 2.0))
        assertIs<GeocoderResult.Success<*>>(geocoder.places(Coordinates(1.0, 2.0)))
        assertEquals(2, fake.reverseCalls)
    }

    @Test
    fun locationsAliasesForward() = runTest {
        val fake = FakePlatformGeocoder(onForward = { listOf(Coordinates(1.0, 1.0)) })

        assertIs<GeocoderResult.Success<Coordinates>>(geocoder(fake).locations("x"))
        assertEquals(1, fake.forwardCalls)
    }

    @Test
    fun emptyListMapsToNotFound() = runTest {
        val geocoder = geocoder(FakePlatformGeocoder())

        assertSame(GeocoderResult.NotFound, geocoder.forward("x"))
        assertSame(GeocoderResult.NotFound, geocoder.reverse(1.0, 1.0))
    }

    @Test
    fun unavailablePlatformMapsToNotSupportedWithoutCallingPlatform() = runTest {
        val fake = FakePlatformGeocoder(available = false)
        val geocoder = geocoder(fake)

        assertSame(GeocoderResult.NotSupported, geocoder.forward("x"))
        assertSame(GeocoderResult.NotSupported, geocoder.reverse(1.0, 1.0))
        assertEquals(0, fake.forwardCalls)
        assertEquals(0, fake.reverseCalls)
    }

    @Test
    fun platformFailuresMapToErrorResults() = runTest {
        val cases: List<Pair<Throwable, GeocoderResult.Error>> = listOf(
            NotSupportedException() to GeocoderResult.NotSupported,
            IllegalArgumentException("bad") to GeocoderResult.InvalidCoordinates,
            NotFoundException() to GeocoderResult.NotFound,
            IllegalStateException("boom") to GeocoderResult.GeocodeFailed("boom"),
            RuntimeException() to GeocoderResult.GeocodeFailed("Unknown error"),
        )

        cases.forEach { (failure, expected) ->
            val geocoder = geocoder(
                FakePlatformGeocoder(
                    onForward = { throw failure },
                    onReverse = { _, _ -> throw failure },
                ),
            )

            assertEquals<GeocoderResult<*>>(expected, geocoder.forward("x"), "forward with $failure")
            assertEquals<GeocoderResult<*>>(expected, geocoder.reverse(1.0, 1.0), "reverse with $failure")
        }
    }

    @Test
    fun cancellationExceptionIsRethrown() = runTest {
        val fake = FakePlatformGeocoder(onForward = { throw CancellationException("cancelled") })

        assertFailsWith<CancellationException> { geocoder(fake).forward("x") }
    }

    @Test
    fun factoryFunctionsCreateWorkingGeocoders() = runTest {
        val fake = FakePlatformGeocoder(
            onForward = { listOf(Coordinates(1.0, 2.0)) },
            onReverse = { _, _ -> listOf(place()) },
        )
        val dispatcher = StandardTestDispatcher(testScheduler)

        assertIs<GeocoderResult.Success<*>>(Geocoder(fake, dispatcher).forward("x"))
        assertIs<GeocoderResult.Success<*>>(ForwardGeocoder(fake, dispatcher).forward("x"))
        assertIs<GeocoderResult.Success<*>>(ReverseGeocoder(fake, dispatcher).reverse(1.0, 2.0))
    }
}

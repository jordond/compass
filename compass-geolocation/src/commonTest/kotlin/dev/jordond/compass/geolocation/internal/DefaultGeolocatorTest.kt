package dev.jordond.compass.geolocation.internal

import dev.jordond.compass.Priority
import dev.jordond.compass.exception.NotFoundException
import dev.jordond.compass.exception.NotSupportedException
import dev.jordond.compass.geolocation.FakeLocator
import dev.jordond.compass.geolocation.GeolocatorResult
import dev.jordond.compass.geolocation.LocationRequest
import dev.jordond.compass.geolocation.exception.GeolocationException
import dev.jordond.compass.geolocation.geolocator
import dev.jordond.compass.permissions.exception.PermissionDeniedException
import dev.jordond.compass.permissions.exception.PermissionDeniedForeverException
import dev.jordond.compass.test.location
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * One-shot requests. The tracking state machine is covered by [DefaultGeolocatorTrackingTest].
 */
class DefaultGeolocatorTest {

    @Test
    fun currentReturnsSuccessWithTheLocatorLocation() = runTest {
        val locator = FakeLocator().apply { onCurrent = { location(10.0, 20.0) } }

        val result = geolocator(locator).current()

        assertEquals(GeolocatorResult.Success(location(10.0, 20.0)), result)
    }

    @Test
    fun currentPassesTheRequestThrough() = runTest {
        val locator = FakeLocator()
        val request = LocationRequest(priority = Priority.HighAccuracy, maximumAge = 42L)

        geolocator(locator).current(request)

        assertEquals(listOf(request), locator.currentRequests)
    }

    @Test
    fun currentWithPriorityBuildsADefaultRequest() = runTest {
        val locator = FakeLocator()

        geolocator(locator).current(Priority.HighAccuracy)

        assertEquals(listOf(LocationRequest(priority = Priority.HighAccuracy)), locator.currentRequests)
    }

    @Test
    fun lastLocationReturnsSuccessWhenLocatorHasOne() = runTest {
        val locator = FakeLocator().apply { onLastLocation = { location(3.0, 4.0) } }

        val result = geolocator(locator).lastLocation(Priority.HighAccuracy)

        assertEquals(GeolocatorResult.Success(location(3.0, 4.0)), result)
        assertEquals(listOf(LocationRequest(priority = Priority.HighAccuracy)), locator.lastLocationRequests)
    }

    @Test
    fun lastLocationReturnsNotFoundWhenLocatorHasNone() = runTest {
        val locator = FakeLocator().apply { onLastLocation = { null } }

        assertEquals(GeolocatorResult.NotFound, geolocator(locator).lastLocation())
    }

    @Test
    fun requestsReportNotSupportedWithoutAskingTheLocatorWhenUnavailable() = runTest {
        val locator = FakeLocator().apply { available = false }
        val geolocator = geolocator(locator)

        assertEquals(GeolocatorResult.NotSupported, geolocator.current())
        assertEquals(GeolocatorResult.NotSupported, geolocator.lastLocation())
        assertEquals(0, locator.currentCalls)
        assertEquals(emptyList(), locator.lastLocationRequests)
    }

    @Test
    fun ignoreAvailableCheckSkipsTheAvailabilityQuery() = runTest {
        val locator = FakeLocator().apply { available = false }
        val geolocator = geolocator(locator)
        val request = LocationRequest(ignoreAvailableCheck = true)

        assertEquals(GeolocatorResult.Success(location()), geolocator.current(request))
        assertEquals(GeolocatorResult.Success(location()), geolocator.lastLocation(request))
        assertEquals(0, locator.availableCalls)
    }

    @Test
    fun isAvailableDelegatesToTheLocator() = runTest {
        val locator = FakeLocator()
        val geolocator = geolocator(locator)

        assertTrue(geolocator.isAvailable())
        locator.available = false
        assertFalse(geolocator.isAvailable())
    }

    @Test
    fun locatorFailuresMapToErrorResults() = runTest {
        val cases: List<Pair<Throwable, GeolocatorResult.Error>> = listOf(
            PermissionDeniedException() to GeolocatorResult.PermissionDenied(false),
            PermissionDeniedForeverException() to GeolocatorResult.PermissionDenied(true),
            NotSupportedException() to GeolocatorResult.NotSupported,
            NotFoundException() to GeolocatorResult.NotFound,
            GeolocationException("no fix") to
                GeolocatorResult.GeolocationFailed("Geolocation failed: no fix"),
            IllegalStateException() to GeolocatorResult.GeolocationFailed("Unknown error"),
        )

        cases.forEach { (failure, expected) ->
            val locator = FakeLocator().apply {
                onCurrent = { throw failure }
                onLastLocation = { throw failure }
            }
            val geolocator = geolocator(locator)

            assertEquals<GeolocatorResult>(expected, geolocator.current(), "current with $failure")
            assertEquals<GeolocatorResult>(expected, geolocator.lastLocation(), "lastLocation with $failure")
        }
    }

    @Test
    fun currentDoesNotSwallowCancellation() = runTest {
        val locator = FakeLocator().apply { onCurrent = { throw CancellationException("cancelled") } }

        assertFailsWith<CancellationException> { geolocator(locator).current() }
    }
}

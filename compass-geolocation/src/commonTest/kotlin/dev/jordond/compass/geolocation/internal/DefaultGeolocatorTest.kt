package dev.jordond.compass.geolocation.internal

import app.cash.turbine.test
import dev.jordond.compass.Location
import dev.jordond.compass.Priority
import dev.jordond.compass.exception.NotFoundException
import dev.jordond.compass.exception.NotSupportedException
import dev.jordond.compass.geolocation.FakeLocator
import dev.jordond.compass.geolocation.GeolocatorResult
import dev.jordond.compass.geolocation.LocationRequest
import dev.jordond.compass.geolocation.TrackingStatus
import dev.jordond.compass.geolocation.exception.GeolocationException
import dev.jordond.compass.geolocation.location
import dev.jordond.compass.permissions.exception.PermissionDeniedException
import dev.jordond.compass.permissions.exception.PermissionDeniedForeverException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultGeolocatorTest {

    private fun TestScope.geolocator(locator: FakeLocator) = DefaultGeolocator(
        locator = locator,
        dispatcher = StandardTestDispatcher(testScheduler),
        coroutineScope = backgroundScope,
    )

    private fun neverEnding(onCancel: () -> Unit = {}): suspend () -> Flow<Location> = {
        flow {
            try {
                awaitCancellation()
            } finally {
                onCancel()
            }
        }
    }

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
    fun currentReportsNotSupportedWithoutAskingTheLocatorWhenUnavailable() = runTest {
        val locator = FakeLocator().apply { available = false }

        val result = geolocator(locator).current()

        assertEquals(GeolocatorResult.NotSupported, result)
        assertEquals(0, locator.currentCalls)
    }

    @Test
    fun ignoreAvailableCheckSkipsTheAvailabilityQuery() = runTest {
        val locator = FakeLocator().apply { available = false }

        val result = geolocator(locator).current(LocationRequest(ignoreAvailableCheck = true))

        assertIs<GeolocatorResult.Success>(result)
        assertEquals(0, locator.availableCalls)
    }

    @Test
    fun lastLocationReturnsNotFoundWhenLocatorHasNone() = runTest {
        val locator = FakeLocator().apply { onLastLocation = { null } }

        assertEquals(GeolocatorResult.NotFound, geolocator(locator).lastLocation())
    }

    @Test
    fun lastLocationReturnsSuccessWhenLocatorHasOne() = runTest {
        val locator = FakeLocator().apply { onLastLocation = { location(3.0, 4.0) } }

        val result = geolocator(locator).lastLocation(Priority.HighAccuracy)

        assertEquals(GeolocatorResult.Success(location(3.0, 4.0)), result)
        assertEquals(listOf(LocationRequest(priority = Priority.HighAccuracy)), locator.lastLocationRequests)
    }

    @Test
    fun lastLocationReportsNotSupportedWhenUnavailable() = runTest {
        val locator = FakeLocator().apply { available = false }

        assertEquals(GeolocatorResult.NotSupported, geolocator(locator).lastLocation())
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
    fun currentMapsPermissionDenied() = runTest {
        val locator = FakeLocator().apply { onCurrent = { throw PermissionDeniedException() } }

        assertEquals(GeolocatorResult.PermissionDenied(false), geolocator(locator).current())
    }

    @Test
    fun currentMapsPermissionDeniedForever() = runTest {
        val locator = FakeLocator().apply { onCurrent = { throw PermissionDeniedForeverException() } }

        assertEquals(GeolocatorResult.PermissionDenied(true), geolocator(locator).current())
    }

    @Test
    fun currentMapsNotSupportedException() = runTest {
        val locator = FakeLocator().apply { onCurrent = { throw NotSupportedException() } }

        assertEquals(GeolocatorResult.NotSupported, geolocator(locator).current())
    }

    @Test
    fun currentMapsNotFoundException() = runTest {
        val locator = FakeLocator().apply { onCurrent = { throw NotFoundException() } }

        assertEquals(GeolocatorResult.NotFound, geolocator(locator).current())
    }

    @Test
    fun currentMapsOtherThrowablesToGeolocationFailedWithTheirMessage() = runTest {
        val locator = FakeLocator().apply { onCurrent = { throw GeolocationException("no fix") } }

        val result = geolocator(locator).current()

        assertEquals(GeolocatorResult.GeolocationFailed("Geolocation failed: no fix"), result)
    }

    @Test
    fun currentMapsThrowableWithoutMessageToUnknownError() = runTest {
        val locator = FakeLocator().apply { onCurrent = { throw IllegalStateException() } }

        assertEquals(GeolocatorResult.GeolocationFailed("Unknown error"), geolocator(locator).current())
    }

    @Test
    fun lastLocationMapsFailuresTheSameWay() = runTest {
        val locator = FakeLocator().apply { onLastLocation = { throw PermissionDeniedForeverException() } }

        assertEquals(GeolocatorResult.PermissionDenied(true), geolocator(locator).lastLocation())
    }

    @Test
    fun currentDoesNotSwallowCancellation() = runTest {
        val locator = FakeLocator().apply { onCurrent = { throw CancellationException("cancelled") } }

        assertFailsWith<CancellationException> { geolocator(locator).current() }
    }

    @Test
    fun trackingStartsIdle() = runTest {
        geolocator(FakeLocator()).trackingStatus.test {
            assertEquals(TrackingStatus.Idle, awaitItem())
            expectNoEvents()
        }
    }

    @Test
    fun trackMovesToTrackingAndAsksTheLocatorOnce() = runTest {
        val locator = FakeLocator().apply { onTrack = neverEnding() }
        val geolocator = geolocator(locator)

        geolocator.track().test {
            assertEquals(TrackingStatus.Idle, awaitItem())
            assertEquals(TrackingStatus.Tracking, awaitItem())
            expectNoEvents()
        }
        assertEquals(1, locator.trackCalls)
    }

    @Test
    fun trackReportsNotSupportedWhenUnavailable() = runTest {
        val locator = FakeLocator().apply { available = false }
        val geolocator = geolocator(locator)

        geolocator.track().test {
            assertEquals(TrackingStatus.Idle, awaitItem())
            assertEquals(TrackingStatus.Error(GeolocatorResult.NotSupported), awaitItem())
        }
        assertEquals(0, locator.trackCalls)
    }

    @Test
    fun trackWithIgnoreAvailableCheckStartsEvenWhenUnavailable() = runTest {
        val locator = FakeLocator().apply {
            available = false
            onTrack = neverEnding()
        }
        val geolocator = geolocator(locator)

        geolocator.track(LocationRequest(ignoreAvailableCheck = true)).test {
            assertEquals(TrackingStatus.Idle, awaitItem())
            assertEquals(TrackingStatus.Tracking, awaitItem())
        }
        assertEquals(1, locator.trackCalls)
    }

    @Test
    fun locatorUpdatesBecomeUpdateStatusesAndLocationUpdates() = runTest {
        val locator = FakeLocator().apply { onTrack = neverEnding() }
        val geolocator = geolocator(locator)

        geolocator.locationUpdates.test {
            geolocator.track()
            runCurrent()
            locator.updates.emit(location(7.0, 8.0))
            assertEquals(location(7.0, 8.0), awaitItem())
            locator.updates.emit(location(9.0, 10.0))
            assertEquals(location(9.0, 10.0), awaitItem())
        }
    }

    @Test
    fun updateStatusIsActive() = runTest {
        val locator = FakeLocator().apply { onTrack = neverEnding() }
        val geolocator = geolocator(locator)

        geolocator.trackingStatus.test {
            assertEquals(TrackingStatus.Idle, awaitItem())
            geolocator.track()
            assertEquals(TrackingStatus.Tracking, awaitItem())
            locator.updates.emit(location())
            val update = awaitItem()
            assertEquals(TrackingStatus.Update(location()), update)
            assertTrue(update.isActive)
        }
    }

    @Test
    fun trackFailureBecomesAnErrorStatus() = runTest {
        val locator = FakeLocator().apply { onTrack = { throw PermissionDeniedException() } }
        val geolocator = geolocator(locator)

        geolocator.trackingStatus.test {
            assertEquals(TrackingStatus.Idle, awaitItem())
            geolocator.track()
            assertEquals(TrackingStatus.Tracking, awaitItem())
            assertEquals(TrackingStatus.Error(GeolocatorResult.PermissionDenied(false)), awaitItem())
        }
    }

    @Test
    fun trackFailureWithPermanentDenialKeepsForeverFlag() = runTest {
        val locator = FakeLocator().apply { onTrack = { throw PermissionDeniedForeverException() } }
        val geolocator = geolocator(locator)

        geolocator.trackingStatus.test {
            assertEquals(TrackingStatus.Idle, awaitItem())
            geolocator.track()
            assertEquals(TrackingStatus.Tracking, awaitItem())
            assertEquals(TrackingStatus.Error(GeolocatorResult.PermissionDenied(true)), awaitItem())
        }
    }

    @Test
    fun trackFailureWithUnknownThrowableBecomesGeolocationFailed() = runTest {
        val locator = FakeLocator().apply { onTrack = { throw GeolocationException("lost") } }
        val geolocator = geolocator(locator)

        geolocator.trackingStatus.test {
            assertEquals(TrackingStatus.Idle, awaitItem())
            geolocator.track()
            assertEquals(TrackingStatus.Tracking, awaitItem())
            assertEquals(
                TrackingStatus.Error(GeolocatorResult.GeolocationFailed("Geolocation failed: lost")),
                awaitItem(),
            )
        }
    }

    @Test
    fun trackingAgainWhileActiveDoesNotRestartTheLocator() = runTest {
        val locator = FakeLocator().apply { onTrack = neverEnding() }
        val geolocator = geolocator(locator)

        geolocator.track()
        runCurrent()
        geolocator.track()
        geolocator.track()
        runCurrent()

        assertEquals(1, locator.trackCalls)
    }

    @Test
    fun stopTrackingResetsToIdleStopsTheLocatorAndCancelsTheFlow() = runTest {
        var cancelled = false
        val locator = FakeLocator().apply { onTrack = neverEnding { cancelled = true } }
        val geolocator = geolocator(locator)

        geolocator.trackingStatus.test {
            assertEquals(TrackingStatus.Idle, awaitItem())
            geolocator.track()
            assertEquals(TrackingStatus.Tracking, awaitItem())
            geolocator.stopTracking()
            assertEquals(TrackingStatus.Idle, awaitItem())
        }
        runCurrent()

        assertEquals(1, locator.stopCalls)
        assertTrue(cancelled)
    }

    @Test
    fun trackingCanStartAgainAfterStop() = runTest {
        val locator = FakeLocator().apply { onTrack = neverEnding() }
        val geolocator = geolocator(locator)

        geolocator.track()
        runCurrent()
        geolocator.stopTracking()
        runCurrent()
        geolocator.track()
        runCurrent()

        assertEquals(2, locator.trackCalls)
    }

    @Test
    fun stopTrackingWithoutTrackingIsHarmless() = runTest {
        val locator = FakeLocator()
        val geolocator = geolocator(locator)

        geolocator.stopTracking()

        geolocator.trackingStatus.test {
            assertEquals(TrackingStatus.Idle, awaitItem())
        }
        assertEquals(1, locator.stopCalls)
    }

    @Test
    fun startTrackingWaitsForTrackingToBegin() = runTest {
        val geolocator = geolocator(FakeLocator().apply { onTrack = neverEnding() })

        assertEquals(TrackingStatus.Tracking, geolocator.startTracking())
    }

    @Test
    fun startTrackingReturnsTheErrorWhenTrackingFails() = runTest {
        val locator = FakeLocator().apply { onTrack = { throw PermissionDeniedException() } }
        val geolocator = geolocator(locator)

        geolocator.trackingStatus.test {
            assertEquals(TrackingStatus.Idle, awaitItem())
            geolocator.track()
            assertEquals(TrackingStatus.Tracking, awaitItem())
            assertEquals(TrackingStatus.Error(GeolocatorResult.PermissionDenied(false)), awaitItem())

            // The retry must report its own outcome, not the error left by the first attempt
            locator.onTrack = neverEnding()
            assertEquals(TrackingStatus.Tracking, geolocator.startTracking())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun errorThrownByTheLocationFlowBecomesAnErrorStatus() = runTest {
        val locator = FakeLocator().apply {
            onTrack = { flow { throw GeolocationException("signal lost") } }
        }
        val geolocator = geolocator(locator)

        geolocator.trackingStatus.test {
            assertEquals(TrackingStatus.Idle, awaitItem())
            geolocator.track()
            assertEquals(TrackingStatus.Tracking, awaitItem())
            assertEquals(
                TrackingStatus.Error(GeolocatorResult.GeolocationFailed("Geolocation failed: signal lost")),
                awaitItem(),
            )
        }
    }
}

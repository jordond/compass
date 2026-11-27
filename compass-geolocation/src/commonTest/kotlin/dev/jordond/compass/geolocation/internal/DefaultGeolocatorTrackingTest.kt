package dev.jordond.compass.geolocation.internal

import app.cash.turbine.test
import dev.jordond.compass.exception.NotFoundException
import dev.jordond.compass.exception.NotSupportedException
import dev.jordond.compass.geolocation.FakeLocator
import dev.jordond.compass.geolocation.Geolocator
import dev.jordond.compass.geolocation.GeolocatorResult
import dev.jordond.compass.geolocation.LocationRequest
import dev.jordond.compass.geolocation.TrackingStatus
import dev.jordond.compass.geolocation.exception.GeolocationException
import dev.jordond.compass.geolocation.geolocator
import dev.jordond.compass.permissions.exception.PermissionDeniedException
import dev.jordond.compass.permissions.exception.PermissionDeniedForeverException
import dev.jordond.compass.test.location
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIsNot

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultGeolocatorTrackingTest {

    /**
     * The status once everything already scheduled has run.
     */
    private suspend fun TestScope.statusOf(geolocator: Geolocator): TrackingStatus {
        runCurrent()
        return geolocator.trackingStatus.first()
    }

    @Test
    fun staysIdleUntilTrackingIsRequestedEvenIfTheLocatorReportsALocation() = runTest {
        val locator = FakeLocator()
        val geolocator = geolocator(locator)

        locator.emit(location())

        assertEquals(TrackingStatus.Idle, statusOf(geolocator))
        assertEquals(0, locator.trackCalls)
    }

    @Test
    fun trackMovesToTrackingAndAsksTheLocatorOnce() = runTest {
        val locator = FakeLocator()
        val geolocator = geolocator(locator)

        geolocator.track().test {
            assertEquals(TrackingStatus.Idle, awaitItem())
            assertEquals(TrackingStatus.Tracking, awaitItem())
        }
        assertEquals(1, locator.trackCalls)
    }

    @Test
    fun trackReportsNotSupportedWithoutAskingTheLocatorWhenUnavailable() = runTest {
        val locator = FakeLocator().apply { available = false }
        val geolocator = geolocator(locator)

        geolocator.track()

        assertEquals(TrackingStatus.Error(GeolocatorResult.NotSupported), statusOf(geolocator))
        assertEquals(0, locator.trackCalls)
    }

    @Test
    fun trackWithIgnoreAvailableCheckStartsWithoutTheAvailabilityQuery() = runTest {
        val locator = FakeLocator().apply { available = false }
        val geolocator = geolocator(locator)

        geolocator.track(LocationRequest(ignoreAvailableCheck = true))

        assertEquals(TrackingStatus.Tracking, statusOf(geolocator))
        assertEquals(1, locator.trackCalls)
        assertEquals(0, locator.availableCalls)
    }

    @Test
    fun locatorUpdatesBecomeUpdateStatusesAndLocationUpdates() = runTest {
        val locator = FakeLocator()
        val geolocator = geolocator(locator)

        geolocator.locationUpdates.test {
            geolocator.track()
            locator.emit(location(7.0, 8.0))
            assertEquals(location(7.0, 8.0), awaitItem())
            assertEquals(TrackingStatus.Update(location(7.0, 8.0)), statusOf(geolocator))

            locator.emit(location(9.0, 10.0))
            assertEquals(location(9.0, 10.0), awaitItem())
            assertEquals(TrackingStatus.Update(location(9.0, 10.0)), statusOf(geolocator))
        }
    }

    @Test
    fun trackingAgainWhileActiveDoesNotRestartTheLocator() = runTest {
        val locator = FakeLocator()
        val geolocator = geolocator(locator)

        geolocator.track()
        runCurrent()
        geolocator.track()
        geolocator.track()
        runCurrent()

        assertEquals(1, locator.trackCalls)
    }

    @Test
    fun failuresStartingTheLocatorBecomeErrorStatuses() = runTest {
        val cases: List<Pair<Throwable, GeolocatorResult.Error>> = listOf(
            PermissionDeniedException() to GeolocatorResult.PermissionDenied(false),
            PermissionDeniedForeverException() to GeolocatorResult.PermissionDenied(true),
            NotSupportedException() to GeolocatorResult.NotSupported,
            NotFoundException() to GeolocatorResult.NotFound,
            GeolocationException("lost") to
                GeolocatorResult.GeolocationFailed("Geolocation failed: lost"),
        )

        cases.forEach { (failure, expected) ->
            val geolocator = geolocator(FakeLocator().apply { onTrack = { throw failure } })

            geolocator.track()

            assertEquals(TrackingStatus.Error(expected), statusOf(geolocator), "track with $failure")
        }
    }

    @Test
    fun errorThrownByTheLocationFlowBecomesAnErrorStatus() = runTest {
        val locator = FakeLocator().apply {
            onTrack = { flow { throw GeolocationException("signal lost") } }
        }
        val geolocator = geolocator(locator)

        geolocator.track()

        assertEquals(
            TrackingStatus.Error(GeolocatorResult.GeolocationFailed("Geolocation failed: signal lost")),
            statusOf(geolocator),
        )
    }

    @Test
    fun locationsReportedAfterAnErrorDoNotHideIt() = runTest {
        val locator = FakeLocator().apply { onTrack = { throw PermissionDeniedException() } }
        val geolocator = geolocator(locator)
        geolocator.track()
        runCurrent()

        locator.emit(location())

        assertEquals(
            TrackingStatus.Error(GeolocatorResult.PermissionDenied(false)),
            statusOf(geolocator),
        )
    }

    @Test
    fun cancellationFromTheLocatorIsNotReportedAsAnErrorAndTrackingCanBeRetried() = runTest {
        val locator = FakeLocator()
        val startsNormally = locator.onTrack
        locator.onTrack = { throw CancellationException("cancelled") }
        val geolocator = geolocator(locator)

        geolocator.track()
        assertIsNot<TrackingStatus.Error>(statusOf(geolocator))

        locator.onTrack = startsNormally
        geolocator.track()
        assertEquals(TrackingStatus.Tracking, statusOf(geolocator))
        assertEquals(2, locator.trackCalls)
    }

    @Test
    fun trackingAgainAfterTheLocationFlowCompletesRestartsTheLocator() = runTest {
        val locator = FakeLocator().apply { onTrack = { emptyFlow() } }
        val geolocator = geolocator(locator)

        geolocator.track()
        runCurrent()
        geolocator.track()
        runCurrent()

        assertEquals(2, locator.trackCalls)
    }

    @Test
    fun startTrackingWaitsForTrackingToBegin() = runTest {
        assertEquals(TrackingStatus.Tracking, geolocator(FakeLocator()).startTracking())
    }

    @Test
    fun startTrackingReturnsTheErrorWhenTrackingCannotStart() = runTest {
        val geolocator = geolocator(FakeLocator().apply { available = false })

        assertEquals(TrackingStatus.Error(GeolocatorResult.NotSupported), geolocator.startTracking())
    }

    @Test
    fun trackingAfterAFailedAttemptReportsTheNewOutcome() = runTest {
        val locator = FakeLocator().apply { available = false }
        val geolocator = geolocator(locator)
        geolocator.track()
        assertEquals(TrackingStatus.Error(GeolocatorResult.NotSupported), statusOf(geolocator))

        // Without clearing the stale error, startTracking would return it instead of waiting
        locator.available = true

        assertEquals(TrackingStatus.Tracking, geolocator.startTracking())
        assertEquals(1, locator.trackCalls)
    }

    @Test
    fun stopTrackingResetsToIdleStopsTheLocatorAndCancelsTheFlow() = runTest {
        val locator = FakeLocator()
        val geolocator = geolocator(locator)
        geolocator.track()
        assertEquals(TrackingStatus.Tracking, statusOf(geolocator))

        geolocator.stopTracking()

        assertEquals(TrackingStatus.Idle, statusOf(geolocator))
        assertEquals(1, locator.stopCalls)
        assertEquals(1, locator.trackCancellations)
    }

    @Test
    fun stopTrackingAfterAnUpdateResetsToIdleAndIgnoresLaterLocations() = runTest {
        val locator = FakeLocator()
        val geolocator = geolocator(locator)
        geolocator.track()
        locator.emit(location(1.0, 1.0))
        assertEquals(TrackingStatus.Update(location(1.0, 1.0)), statusOf(geolocator))

        geolocator.stopTracking()
        assertEquals(TrackingStatus.Idle, statusOf(geolocator))

        // Platform locators can deliver a fix that was already in flight when tracking stopped
        locator.emit(location(2.0, 2.0))
        assertEquals(TrackingStatus.Idle, statusOf(geolocator))
    }

    @Test
    fun trackingCanStartAgainAfterStopAndReportsLocationsAgain() = runTest {
        val locator = FakeLocator()
        val geolocator = geolocator(locator)
        geolocator.track()
        runCurrent()
        geolocator.stopTracking()
        runCurrent()

        geolocator.track()
        assertEquals(TrackingStatus.Tracking, statusOf(geolocator))
        locator.emit(location(3.0, 3.0))

        assertEquals(TrackingStatus.Update(location(3.0, 3.0)), statusOf(geolocator))
        assertEquals(2, locator.trackCalls)
    }

    @Test
    fun trackStopTrackBeforeAnythingRunsStartsTheLocatorExactlyOnce() = runTest {
        val locator = FakeLocator()
        val geolocator = geolocator(locator)

        geolocator.track()
        geolocator.stopTracking()
        geolocator.track()

        assertEquals(TrackingStatus.Tracking, statusOf(geolocator))
        assertEquals(1, locator.trackCalls)
    }

    @Test
    fun trackThenStopBeforeAnythingRunsNeverStartsTheLocator() = runTest {
        val locator = FakeLocator()
        val geolocator = geolocator(locator)

        geolocator.track()
        geolocator.stopTracking()

        assertEquals(TrackingStatus.Idle, statusOf(geolocator))
        assertEquals(0, locator.trackCalls)
    }

    @Test
    fun stopTrackingWithoutTrackingIsHarmless() = runTest {
        val locator = FakeLocator()
        val geolocator = geolocator(locator)

        geolocator.stopTracking()

        assertEquals(TrackingStatus.Idle, statusOf(geolocator))
        assertEquals(1, locator.stopCalls)
    }
}

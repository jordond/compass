package dev.jordond.compass.geolocation

import dev.jordond.compass.Coordinates
import dev.jordond.compass.Location
import dev.jordond.compass.exception.NotFoundException
import dev.jordond.compass.permissions.LocationAccuracy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

internal fun location(latitude: Double = 1.0, longitude: Double = 2.0) = Location(
    coordinates = Coordinates(latitude, longitude),
    accuracy = 5.0,
    azimuth = null,
    speed = null,
    mslAltitude = null,
    ellipsoidalAltitude = null,
    timestampMillis = 0L,
)

class GeolocatorResultTest {

    @Test
    fun successExposesLocationAndIsNotAnError() {
        val result: GeolocatorResult = GeolocatorResult.Success(location())

        assertFalse(result.isError)
        assertEquals(location(), result.getOrNull())
        assertNull(result.errorOrNull())
    }

    @Test
    fun errorsExposeThemselvesAndNoLocation() {
        val errors = listOf(
            GeolocatorResult.NotFound,
            GeolocatorResult.NotSupported,
            GeolocatorResult.GeolocationFailed("boom"),
            GeolocatorResult.PermissionDenied(false),
        )

        errors.forEach { error ->
            assertTrue(error.isError)
            assertNull(error.getOrNull())
            assertSame(error, error.errorOrNull())
        }
    }

    @Test
    fun onSuccessOnlyRunsForSuccess() {
        var calls = 0
        GeolocatorResult.Success(location()).onSuccess { calls++ }
        GeolocatorResult.NotFound.onSuccess { calls++ }

        assertEquals(1, calls)
    }

    @Test
    fun onFailedOnlyRunsForErrors() {
        var calls = 0
        GeolocatorResult.Success(location()).onFailed { calls++ }
        GeolocatorResult.NotSupported.onFailed { calls++ }

        assertEquals(1, calls)
    }

    @Test
    fun permissionDeniedMessageDependsOnForever() {
        assertEquals("Permission denied by user.", GeolocatorResult.PermissionDenied(false).message)
        assertEquals(
            "Permission denied forever by user.",
            GeolocatorResult.PermissionDenied(true).message,
        )
    }

    @Test
    fun valueClassesCompareStructurally() {
        assertEquals(GeolocatorResult.Success(location()), GeolocatorResult.Success(location()))
        assertEquals(GeolocatorResult.PermissionDenied(true), GeolocatorResult.PermissionDenied(true))
        assertEquals(GeolocatorResult.GeolocationFailed("a"), GeolocatorResult.GeolocationFailed("a"))
    }
}

class TrackingStatusTest {

    @Test
    fun onlyTrackingAndUpdateAreActive() {
        assertTrue(TrackingStatus.Tracking.isActive)
        assertTrue(TrackingStatus.Update(location()).isActive)
        assertFalse(TrackingStatus.Idle.isActive)
        assertFalse(TrackingStatus.Error(GeolocatorResult.NotFound).isActive)
    }
}

class ExtensionsTest {

    @Test
    fun permissionDeniedHelpers() {
        assertTrue(GeolocatorResult.PermissionDenied(false).isPermissionDenied())
        assertFalse(GeolocatorResult.PermissionDenied(false).isPermissionDeniedForever())
        assertTrue(GeolocatorResult.PermissionDenied(true).isPermissionDeniedForever())
        assertFalse(GeolocatorResult.NotFound.isPermissionDenied())
        assertFalse(GeolocatorResult.NotFound.isPermissionDeniedForever())
    }

    @Test
    fun hasPermissionDefaultsToTrueForPlainLocators() {
        assertTrue(Geolocator(FakeLocator()).hasPermission())
    }

    @Test
    fun hasPermissionAndAccuracyComeFromAPermissionLocator() {
        val locator = FakePermissionLocator(granted = false, accuracy = LocationAccuracy.Reduced)
        val geolocator = Geolocator(locator)

        assertFalse(geolocator.hasPermission())
        assertEquals(LocationAccuracy.Reduced, geolocator.grantedAccuracy())
    }

    @Test
    fun accuracyDefaultsToUnknownForPlainLocators() {
        assertEquals(LocationAccuracy.Unknown, Geolocator(FakeLocator()).grantedAccuracy())
    }

    @Test
    fun currentLocationOrNullMapsFailureToNull() = runTest {
        val failing = FakeLocator().apply { onCurrent = { throw NotFoundException() } }
        val working = FakeLocator().apply { onCurrent = { location() } }

        assertNull(Geolocator(failing, Dispatchers.Unconfined).currentLocationOrNull())
        assertEquals(location(), Geolocator(working, Dispatchers.Unconfined).currentLocationOrNull())
    }
}

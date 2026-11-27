package dev.jordond.compass.geolocation

import dev.jordond.compass.exception.NotFoundException
import dev.jordond.compass.permissions.LocationAccuracy
import dev.jordond.compass.test.location
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ExtensionsTest {

    @Test
    fun permissionDeniedHelpersReadTheErrorTypeAndItsForeverFlag() {
        assertTrue(GeolocatorResult.PermissionDenied(false).isPermissionDenied())
        assertFalse(GeolocatorResult.PermissionDenied(false).isPermissionDeniedForever())
        assertTrue(GeolocatorResult.PermissionDenied(true).isPermissionDeniedForever())
        assertFalse(GeolocatorResult.NotFound.isPermissionDenied())
        assertFalse(GeolocatorResult.NotFound.isPermissionDeniedForever())
    }

    @Test
    fun plainLocatorsAreTreatedAsPermittedWithUnknownAccuracy() = runTest {
        val geolocator = geolocator(FakeLocator())

        assertTrue(geolocator.hasPermission())
        assertEquals(LocationAccuracy.Unknown, geolocator.grantedAccuracy())
    }

    @Test
    fun permissionAndAccuracyComeFromAPermissionLocator() = runTest {
        val locator = FakePermissionLocator(granted = false, accuracy = LocationAccuracy.Reduced)
        val geolocator = geolocator(locator)

        assertFalse(geolocator.hasPermission())
        assertEquals(LocationAccuracy.Reduced, geolocator.grantedAccuracy())
    }

    @Test
    fun currentLocationOrNullMapsFailureToNull() = runTest {
        val failing = FakeLocator().apply { onCurrent = { throw NotFoundException() } }
        val working = FakeLocator().apply { onCurrent = { location() } }

        assertNull(geolocator(failing).currentLocationOrNull())
        assertEquals(location(), geolocator(working).currentLocationOrNull())
    }
}

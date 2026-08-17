package dev.jordond.compass.permissions.mobile.internal

import dev.jordond.compass.permissions.LocationAccuracy
import kotlin.test.Test
import kotlin.test.assertEquals

class GrantedAccuracyTest {

    @Test
    fun reportsFullWhenThePrecisePermissionIsHeld() {
        assertEquals(
            LocationAccuracy.Full,
            resolveGrantedAccuracy(granted = true, fullAccuracy = true),
        )
    }

    @Test
    fun reportsReducedWhenTheGrantedPermissionIsNotThePreciseOne() {
        // The defect this exists for. iOS answers `Granted` for a user who turned Precise Location
        // off, and Android does the same for one who answered "Approximate", so the permission
        // state alone cannot tell a caller its fixes are about to be kilometre scale.
        assertEquals(
            LocationAccuracy.Reduced,
            resolveGrantedAccuracy(granted = true, fullAccuracy = false),
        )
    }

    @Test
    fun reportsFullWhenThePlatformCannotDistinguishAccuracy() {
        // Reduced accuracy arrived in iOS 14. Below that a granted permission is always precise,
        // so `Unknown` would understate what the app actually holds.
        assertEquals(
            LocationAccuracy.Full,
            resolveGrantedAccuracy(granted = true, fullAccuracy = null),
        )
    }

    @Test
    fun reportsUnknownWithoutAnyPermission() {
        assertEquals(
            LocationAccuracy.Unknown,
            resolveGrantedAccuracy(granted = false, fullAccuracy = null),
        )
    }

    @Test
    fun reportsUnknownWithoutPermissionEvenWhenTheSystemClaimsFullAccuracy() {
        // CoreLocation reports full accuracy while the status is still `notDetermined`, before the
        // user has answered anything. Taking that at face value would have `grantedAccuracy()`
        // promise precise location to an app holding no permission at all.
        assertEquals(
            LocationAccuracy.Unknown,
            resolveGrantedAccuracy(granted = false, fullAccuracy = true),
        )
    }

    @Test
    fun reportsUnknownWithoutPermissionEvenWhenAccuracyIsReduced() {
        assertEquals(
            LocationAccuracy.Unknown,
            resolveGrantedAccuracy(granted = false, fullAccuracy = false),
        )
    }
}

package dev.jordond.compass.geolocation.mobile

import android.content.Context
import android.location.Location
import androidx.core.location.LocationCompat
import dev.jordond.compass.Coordinates
import dev.jordond.compass.Priority
import dev.jordond.compass.geolocation.LocationRequest
import dev.jordond.compass.permissions.LocationAccuracy
import dev.jordond.compass.permissions.LocationPermissionController
import dev.jordond.compass.permissions.PermissionState
import dev.jordond.compass.permissions.exception.PermissionDeniedException
import dev.jordond.compass.permissions.exception.PermissionDeniedForeverException
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AndroidLocatorTest {

    private val context = mockk<Context>()
    private val updates = MutableSharedFlow<Location>()
    private val provider = mockk<PlatformLocationProvider>(relaxed = true) {
        every { locationUpdates } returns updates
    }
    private val permissions = mockk<LocationPermissionController>()

    @BeforeTest
    fun setUp() {
        coEvery { permissions.requirePermissionFor(any()) } returns PermissionState.Granted
    }

    @AfterTest
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun hasPermissionAndGrantedAccuracyDelegateToTheController() {
        every { permissions.hasPermission() } returns true
        every { permissions.grantedAccuracy() } returns LocationAccuracy.Reduced

        assertTrue(locator().hasPermission())
        assertEquals(LocationAccuracy.Reduced, locator().grantedAccuracy())
    }

    @Test
    fun isAvailableFollowsTheProvider() = runTest {
        coEvery { provider.locationEnabled() } returns false

        assertFalse(locator().isAvailable())
    }

    @Test
    fun lastLocationIsNullWhenTheProviderHoldsNone() = runTest {
        coEvery { provider.lastLocation() } returns null

        assertNull(locator().lastLocation(Priority.Balanced))
    }

    @Test
    fun deniedPermissionStopsEveryLocationRequestBeforeTheProvider() = runTest {
        coEvery { permissions.requirePermissionFor(any()) } returns PermissionState.Denied
        val locator = locator()

        assertFailsWith<PermissionDeniedException> { locator.lastLocation(Priority.Balanced) }
        assertFailsWith<PermissionDeniedException> { locator.current(Priority.Balanced) }
        assertFailsWith<PermissionDeniedException> { locator.track(LocationRequest()) }

        coVerify(exactly = 0) { provider.lastLocation() }
        coVerify(exactly = 0) { provider.currentLocation(any()) }
        verify(exactly = 0) { provider.startTracking(any()) }
    }

    @Test
    fun foreverDeniedPermissionIsReportedAsSuch() = runTest {
        coEvery { permissions.requirePermissionFor(any()) } returns PermissionState.DeniedForever

        assertFailsWith<PermissionDeniedForeverException> { locator().current(Priority.HighAccuracy) }
    }

    @Test
    fun permissionIsRequestedForTheRequestedPriority() = runTest {
        coEvery { provider.lastLocation() } returns null

        locator().lastLocation(Priority.HighAccuracy)

        coVerify(exactly = 1) { permissions.requirePermissionFor(Priority.HighAccuracy) }
    }

    @Test
    fun trackStartsTheProviderWithTheRequestAfterThePermissionCheck() = runTest {
        val request = LocationRequest(priority = Priority.LowPower, interval = 9_000L)

        locator().track(request)

        coVerify(exactly = 1) { permissions.requirePermissionFor(Priority.LowPower) }
        verify(exactly = 1) { provider.startTracking(request) }
    }

    @Test
    fun stopTrackingDelegatesToTheProvider() {
        locator().stopTracking()

        verify(exactly = 1) { provider.stopTracking() }
    }

    @Test
    fun currentWithoutAMaximumAgeAsksTheProviderForAFreshFix() = runTest {
        mockLocation()
        coEvery { provider.currentLocation(Priority.Balanced) } returns Location("gps")

        val location = locator().current(LocationRequest(priority = Priority.Balanced))

        assertEquals(Coordinates(1.0, 2.0), location.coordinates)
        coVerify(exactly = 0) { provider.lastLocation() }
    }

    @Test
    fun currentServesARecentCachedFixWithinTheMaximumAge() = runTest {
        mockLocation(timeMillis = System.currentTimeMillis())
        coEvery { provider.lastLocation() } returns Location("gps")

        locator().current(LocationRequest(maximumAge = 60_000L))

        coVerify(exactly = 0) { provider.currentLocation(any()) }
    }

    @Test
    fun currentFallsThroughToAFreshFixWhenTheCachedOneIsTooOld() = runTest {
        mockLocation(timeMillis = 1L)
        coEvery { provider.lastLocation() } returns Location("gps")
        coEvery { provider.currentLocation(any()) } returns Location("gps")

        locator().current(LocationRequest(maximumAge = 1_000L))

        coVerify(exactly = 1) { provider.currentLocation(Priority.Balanced) }
    }

    private fun locator() = AndroidLocator(context, permissions, provider)

    private fun mockLocation(timeMillis: Long = 0L) {
        mockkConstructor(Location::class)
        mockkStatic(LocationCompat::class)
        every { anyConstructed<Location>().latitude } returns 1.0
        every { anyConstructed<Location>().longitude } returns 2.0
        every { anyConstructed<Location>().accuracy } returns 3f
        every { anyConstructed<Location>().time } returns timeMillis
        every { anyConstructed<Location>().hasBearing() } returns false
        every { anyConstructed<Location>().hasSpeed() } returns false
        every { anyConstructed<Location>().hasAltitude() } returns false
        every { LocationCompat.hasMslAltitude(any()) } returns false
    }
}

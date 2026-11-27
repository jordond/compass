package dev.jordond.compass.geolocation.mobile

import android.content.Context
import android.location.Location
import dev.jordond.compass.Coordinates
import dev.jordond.compass.Priority
import dev.jordond.compass.geolocation.LocationRequest
import dev.jordond.compass.permissions.LocationPermissionController
import dev.jordond.compass.permissions.PermissionState
import dev.jordond.compass.permissions.exception.PermissionDeniedException
import dev.jordond.compass.permissions.exception.PermissionDeniedForeverException
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
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
    fun locationUpdatesMapsThePlatformLocationsOfTheProvider() = runTest {
        val platformLocation = mockPlatformLocation(latitude = 43.0, longitude = -79.0)
        val locator = locator()

        val received = async { locator.locationUpdates.first() }
        runCurrent()
        updates.emit(platformLocation)

        assertEquals(Coordinates(43.0, -79.0), received.await().coordinates)
    }

    @Test
    fun trackStartsTheProviderForTheRequestAndReturnsItsUpdates() = runTest {
        val platformLocation = mockPlatformLocation(latitude = 43.0, longitude = -79.0)
        val request = LocationRequest(priority = Priority.LowPower, interval = 9_000L)

        val tracked = locator().track(request)

        coVerify(exactly = 1) { permissions.requirePermissionFor(Priority.LowPower) }
        verify(exactly = 1) { provider.startTracking(request) }

        val received = async { tracked.first() }
        runCurrent()
        updates.emit(platformLocation)

        assertEquals(Coordinates(43.0, -79.0), received.await().coordinates)
    }

    @Test
    fun currentWithoutAMaximumAgeAsksTheProviderForAFreshFix() = runTest {
        coEvery { provider.currentLocation(Priority.Balanced) } returns mockPlatformLocation()

        val location = locator().current(LocationRequest(priority = Priority.Balanced))

        assertEquals(Coordinates(1.0, 2.0), location.coordinates)
        coVerify(exactly = 0) { provider.lastLocation() }
    }

    // The age rules themselves are covered by CachedLocationTest. This only proves the Android
    // locator consults the cache, so the maximum age is wide enough to make the clock irrelevant.
    @Test
    fun currentServesTheCachedFixWhenItIsWithinTheMaximumAge() = runTest {
        coEvery { provider.lastLocation() } returns mockPlatformLocation(timeMillis = 1L)

        val location = locator().current(LocationRequest(maximumAge = Long.MAX_VALUE))

        assertEquals(Coordinates(1.0, 2.0), location.coordinates)
        coVerify(exactly = 0) { provider.currentLocation(any()) }
    }

    private fun locator() = AndroidLocator(context, permissions, provider)
}

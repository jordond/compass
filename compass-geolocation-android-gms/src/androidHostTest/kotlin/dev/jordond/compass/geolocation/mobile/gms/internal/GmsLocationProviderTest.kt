package dev.jordond.compass.geolocation.mobile.gms.internal

import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import androidx.core.location.LocationManagerCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.Priority as GmsPriority
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.CancellationToken
import dev.jordond.compass.Priority
import dev.jordond.compass.exception.NotFoundException
import dev.jordond.compass.geolocation.mobile.gms.fakeTask
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue
import dev.jordond.compass.geolocation.LocationRequest as CompassLocationRequest

@OptIn(ExperimentalCoroutinesApi::class)
class GmsLocationProviderTest {

    private val context = mockk<Context>()
    private val fused = mockk<FusedLocationProviderClient>(relaxed = true)
    private val settings = mockk<SettingsClient>()
    private val locationManager = mockk<LocationManager>()
    private val looper = mockk<Looper>()

    private val gmsRequest = slot<LocationRequest>()
    private val callback = slot<LocationCallback>()

    @BeforeTest
    fun setUp() {
        mockkStatic(LocationServices::class)
        mockkStatic(LocationManagerCompat::class)
        mockkStatic(Looper::class)
        every { context.applicationContext } returns context
        every { context.getSystemService(Context.LOCATION_SERVICE) } returns locationManager
        every { LocationServices.getFusedLocationProviderClient(context) } returns fused
        every { LocationServices.getSettingsClient(context) } returns settings
        every { Looper.getMainLooper() } returns looper
        every {
            fused.requestLocationUpdates(capture(gmsRequest), capture(callback), looper)
        } returns mockk(relaxed = true)
    }

    @AfterTest
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun lastLocationResumesWithTheFusedFix() = runTest {
        val fix = mockk<Location>()
        every { fused.lastLocation } returns fakeTask(result = fix)

        assertSame(fix, provider().lastLocation())
    }

    @Test
    fun lastLocationIsNullWhenTheClientHasNone() = runTest {
        every { fused.lastLocation } returns fakeTask<Location>(result = null)

        assertNull(provider().lastLocation())
    }

    @Test
    fun lastLocationRethrowsClientFailures() = runTest {
        every { fused.lastLocation } returns
            fakeTask<Location>(failure = IllegalStateException("denied"))

        assertEquals(
            "denied",
            assertFailsWith<IllegalStateException> { provider().lastLocation() }.message,
        )
    }

    @Test
    fun lastLocationSurfacesACancelledTaskAsCancellation() = runTest {
        every { fused.lastLocation } returns fakeTask<Location>(cancelled = true)

        assertFailsWith<CancellationException> { provider().lastLocation() }
    }

    @Test
    fun locationEnabledIsTrueWhenTheSettingsCheckPasses() = runTest {
        every { settings.checkLocationSettings(any()) } returns
            fakeTask(mockk<LocationSettingsResponse>())

        assertTrue(provider().locationEnabled())
    }

    @Test
    fun locationEnabledFallsBackToThePlatformSettingWhenTheCheckFails() = runTest {
        every { settings.checkLocationSettings(any()) } returns
            fakeTask<LocationSettingsResponse>(failure = RuntimeException("resolution required"))
        every { LocationManagerCompat.isLocationEnabled(locationManager) } returns true

        assertTrue(provider().locationEnabled())
    }

    @Test
    fun locationEnabledIsFalseWhenBothTheCheckAndThePlatformSayOff() = runTest {
        every { settings.checkLocationSettings(any()) } returns
            fakeTask<LocationSettingsResponse>(failure = RuntimeException("off"))
        every { LocationManagerCompat.isLocationEnabled(locationManager) } returns false

        assertFalse(provider().locationEnabled())
    }

    @Test
    fun currentLocationRequestsTheMatchingGmsPriority() = runTest {
        val expected = mapOf(
            Priority.HighAccuracy to GmsPriority.PRIORITY_HIGH_ACCURACY,
            Priority.Balanced to GmsPriority.PRIORITY_BALANCED_POWER_ACCURACY,
            Priority.LowPower to GmsPriority.PRIORITY_LOW_POWER,
            Priority.Passive to GmsPriority.PRIORITY_PASSIVE,
        )
        val provider = provider()

        for ((priority, gmsPriority) in expected) {
            val fix = mockk<Location>()
            every { fused.getCurrentLocation(gmsPriority, any<CancellationToken>()) } returns
                fakeTask(fix)

            assertSame(fix, provider.currentLocation(priority), "fix for $priority")
        }
    }

    @Test
    fun cancellingCurrentLocationCancelsTheFusedRequest() = runTest {
        val token = slot<CancellationToken>()
        every { fused.getCurrentLocation(any<Int>(), capture(token)) } returns
            fakeTask<Location>(complete = false)
        val provider = provider()

        val job = launch { provider.currentLocation(Priority.Balanced) }
        runCurrent()
        assertFalse(token.captured.isCancellationRequested)

        job.cancelAndJoin()

        assertTrue(token.captured.isCancellationRequested)
    }

    @Test
    fun currentLocationThrowsNotFoundWhenTheClientReturnsNull() = runTest {
        every { fused.getCurrentLocation(any<Int>(), any<CancellationToken>()) } returns
            fakeTask<Location>(result = null)

        assertFailsWith<NotFoundException> { provider().currentLocation(Priority.Balanced) }
    }

    @Test
    fun startTrackingRegistersOnceWithAnIntervalMatchedRequest() {
        val provider = provider()
        val request = CompassLocationRequest(priority = Priority.HighAccuracy, interval = 4_000L)

        provider.startTracking(request)
        provider.startTracking(request)

        verifyRegistrations(1)
        assertEquals(GmsPriority.PRIORITY_HIGH_ACCURACY, gmsRequest.captured.priority)
        assertEquals(4_000L, gmsRequest.captured.intervalMillis)
        assertEquals(4_000L, gmsRequest.captured.minUpdateIntervalMillis)
    }

    @Test
    fun stopTrackingRemovesTheCallbackAndAllowsANewSession() {
        val provider = provider()

        provider.startTracking(CompassLocationRequest())
        val registered = callback.captured
        provider.stopTracking()
        provider.startTracking(CompassLocationRequest())

        verify(exactly = 1) { fused.removeLocationUpdates(registered) }
        verifyRegistrations(2)
    }

    @Test
    fun stopTrackingWithoutASessionDoesNothing() {
        provider().stopTracking()

        verify(exactly = 0) { fused.removeLocationUpdates(any<LocationCallback>()) }
    }

    @Test
    fun locationResultsAreEmittedToLocationUpdates() = runTest {
        val provider = provider()
        val received = mutableListOf<Location>()
        val job = launch { provider.locationUpdates.collect { location -> received += location } }

        provider.startTracking(CompassLocationRequest())
        val fix = mockk<Location>()
        callback.captured.onLocationResult(locationResult(fix))
        callback.captured.onLocationResult(locationResult(null))
        runCurrent()

        assertEquals(listOf(fix), received)
        job.cancel()
    }

    private fun provider() = GmsLocationProvider(context)

    private fun locationResult(last: Location?): LocationResult = mockk {
        every { lastLocation } returns last
    }

    private fun verifyRegistrations(count: Int) {
        verify(exactly = count) {
            fused.requestLocationUpdates(any<LocationRequest>(), any<LocationCallback>(), looper)
        }
    }
}

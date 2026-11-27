package dev.jordond.compass.geolocation.mobile.internal

import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.location.Location
import android.location.LocationManager
import android.os.CancellationSignal
import androidx.core.content.ContextCompat
import androidx.core.location.LocationListenerCompat
import androidx.core.location.LocationManagerCompat
import androidx.core.location.LocationRequestCompat
import androidx.core.util.Consumer
import dev.jordond.compass.Priority
import dev.jordond.compass.exception.NotFoundException
import dev.jordond.compass.geolocation.LocationRequest
import io.mockk.every
import io.mockk.just
import io.mockk.Runs
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import java.util.concurrent.Executor
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

// SDK_INT is 0 on the host, so provider selection never takes the fused provider branch.
class StandardLocationProviderTest {

    private val executor = mockk<Executor>()
    private val locationManager = mockk<LocationManager>()
    private val context = mockk<Context>(relaxed = true)

    private var locationEnabled = true
    private var enabledProviders = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)

    @BeforeTest
    fun setUp() {
        mockkStatic(ContextCompat::class)
        mockkStatic(LocationManagerCompat::class)
        mockkConstructor(CancellationSignal::class)
        mockkConstructor(IntentFilter::class)
        mockkConstructor(BroadcastReceiver::class)

        every { context.applicationContext } returns context
        every { context.getSystemService(Context.LOCATION_SERVICE) } returns locationManager
        every { ContextCompat.getMainExecutor(any()) } returns executor
        every { ContextCompat.registerReceiver(any(), any(), any(), any()) } returns null
        every { anyConstructed<IntentFilter>().addAction(any()) } just Runs
        every { anyConstructed<CancellationSignal>().cancel() } just Runs
        every { LocationManagerCompat.isLocationEnabled(locationManager) } answers { locationEnabled }
        every { locationManager.getProviders(true) } answers { enabledProviders }
        every { LocationManagerCompat.requestLocationUpdates(any(), any<String>(), any<LocationRequestCompat>(), any<Executor>(), any<LocationListenerCompat>()) } just Runs
        every { LocationManagerCompat.removeUpdates(any(), any()) } just Runs
    }

    @AfterTest
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun lastLocationPicksTheMostRecentFixAcrossProviders() = runTest {
        val old = location(elapsedNanos = 100)
        val fresh = location(elapsedNanos = 900)
        every { locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) } returns old
        every { locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) } returns fresh

        assertSame(fresh, provider().lastLocation())
    }

    @Test
    fun lastLocationSkipsProvidersWithoutAFix() = runTest {
        val only = location(elapsedNanos = 5)
        every { locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) } returns null
        every { locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) } returns only

        assertSame(only, provider().lastLocation())
    }

    @Test
    fun lastLocationIsNullWhenNoProviderHoldsOne() = runTest {
        every { locationManager.getLastKnownLocation(any()) } returns null

        assertNull(provider().lastLocation())
    }

    @Test
    fun locationEnabledFollowsTheSystemSetting() = runTest {
        val provider = provider()

        locationEnabled = false
        assertFalse(provider.locationEnabled())

        locationEnabled = true
        assertTrue(provider.locationEnabled())
    }

    @Test
    fun currentLocationThrowsNotFoundWhenNoProviderCanServeIt() = runTest {
        enabledProviders = emptyList()

        assertFailsWith<NotFoundException> { provider().currentLocation(Priority.HighAccuracy) }
    }

    @Test
    fun currentLocationThrowsNotFoundWhenLocationIsOff() = runTest {
        locationEnabled = false

        assertFailsWith<NotFoundException> { provider().currentLocation(Priority.Balanced) }
    }

    @Test
    fun currentLocationRequestsTheBestProviderAndReturnsTheFix() = runTest {
        val fix = location()
        val requested = slot<String>()
        stubCurrentLocation(requested) { consumer -> consumer.accept(fix) }

        val result = provider().currentLocation(Priority.HighAccuracy)

        assertSame(fix, result)
        assertEquals(LocationManager.GPS_PROVIDER, requested.captured)
    }

    @Test
    fun currentLocationThrowsNotFoundWhenTheProviderReturnsNothing() = runTest {
        stubCurrentLocation(slot<String>()) { consumer -> consumer.accept(null) }

        assertFailsWith<NotFoundException> { provider().currentLocation(Priority.HighAccuracy) }
    }

    @Test
    fun startTrackingRegistersOnTheBestProviderOnce() {
        val provider = provider()
        val request = LocationRequest(priority = Priority.Balanced, interval = 2_000L)

        provider.startTracking(request)
        provider.startTracking(request)

        verify(exactly = 1) {
            LocationManagerCompat.requestLocationUpdates(
                locationManager,
                LocationManager.NETWORK_PROVIDER,
                any<LocationRequestCompat>(),
                executor,
                any<LocationListenerCompat>(),
            )
        }
    }

    @Test
    fun startTrackingBuildsTheRequestFromTheCompassRequest() {
        val captured = slot<LocationRequestCompat>()
        every {
            LocationManagerCompat.requestLocationUpdates(any(), any<String>(), capture(captured), any<Executor>(), any<LocationListenerCompat>())
        } just Runs

        provider().startTracking(LocationRequest(priority = Priority.HighAccuracy, interval = 3_000L))

        assertEquals(3_000L, captured.captured.intervalMillis)
        assertEquals(3_000L, captured.captured.minUpdateIntervalMillis)
        assertEquals(LocationRequestCompat.QUALITY_HIGH_ACCURACY, captured.captured.quality)
    }

    @Test
    fun startTrackingThrowsNotFoundWhenNoProviderCanServeTheRequest() {
        enabledProviders = emptyList()

        assertFailsWith<NotFoundException> { provider().startTracking(LocationRequest()) }
    }

    @Test
    fun aFailedStartTrackingCanBeRetried() {
        val provider = provider()
        enabledProviders = emptyList()
        assertFailsWith<NotFoundException> { provider.startTracking(LocationRequest()) }

        enabledProviders = listOf(LocationManager.GPS_PROVIDER)
        provider.startTracking(LocationRequest())

        verify(exactly = 1) {
            LocationManagerCompat.requestLocationUpdates(any(), any<String>(), any<LocationRequestCompat>(), any<Executor>(), any<LocationListenerCompat>())
        }
    }

    @Test
    fun stopTrackingRemovesTheListenerAndAllowsANewSession() {
        val provider = provider()

        provider.startTracking(LocationRequest())
        provider.stopTracking()
        provider.startTracking(LocationRequest())

        verify(exactly = 1) { LocationManagerCompat.removeUpdates(locationManager, any()) }
        verify(exactly = 2) {
            LocationManagerCompat.requestLocationUpdates(any(), any<String>(), any<LocationRequestCompat>(), any<Executor>(), any<LocationListenerCompat>())
        }
    }

    @Test
    fun stopTrackingWithoutASessionDoesNothing() {
        provider().stopTracking()

        verify(exactly = 0) { LocationManagerCompat.removeUpdates(any(), any()) }
    }

    @Test
    fun locationChangesAreEmittedToLocationUpdates() = runTest {
        val listener = slot<LocationListenerCompat>()
        every {
            LocationManagerCompat.requestLocationUpdates(any(), any<String>(), any<LocationRequestCompat>(), any<Executor>(), capture(listener))
        } just Runs
        val provider = provider()
        val received = mutableListOf<Location>()
        val job = launch { provider.locationUpdates.collect { received += it } }

        provider.startTracking(LocationRequest())
        val fix = location()
        listener.captured.onLocationChanged(fix)
        testScheduler.advanceUntilIdle()

        assertEquals(listOf(fix), received)
        job.cancel()
    }

    @Test
    fun aDisabledProviderMovesTheSessionToTheNextBestOne() {
        val listener = slot<LocationListenerCompat>()
        every {
            LocationManagerCompat.requestLocationUpdates(any(), any<String>(), any<LocationRequestCompat>(), any<Executor>(), capture(listener))
        } just Runs
        val provider = provider()

        provider.startTracking(LocationRequest(priority = Priority.HighAccuracy))
        enabledProviders = listOf(LocationManager.NETWORK_PROVIDER)
        listener.captured.onProviderDisabled(LocationManager.GPS_PROVIDER)

        verify(exactly = 1) { LocationManagerCompat.removeUpdates(locationManager, any()) }
        verify(exactly = 1) {
            LocationManagerCompat.requestLocationUpdates(
                locationManager,
                LocationManager.NETWORK_PROVIDER,
                any<LocationRequestCompat>(),
                executor,
                any<LocationListenerCompat>(),
            )
        }
    }

    @Test
    fun aRefreshWithTheSameBestProviderDoesNotReRegister() {
        val listener = slot<LocationListenerCompat>()
        every {
            LocationManagerCompat.requestLocationUpdates(any(), any<String>(), any<LocationRequestCompat>(), any<Executor>(), capture(listener))
        } just Runs
        val provider = provider()

        provider.startTracking(LocationRequest(priority = Priority.HighAccuracy))
        listener.captured.onProviderEnabled(LocationManager.NETWORK_PROVIDER)

        verify(exactly = 1) {
            LocationManagerCompat.requestLocationUpdates(any(), any<String>(), any<LocationRequestCompat>(), any<Executor>(), any<LocationListenerCompat>())
        }
        verify(exactly = 0) { LocationManagerCompat.removeUpdates(any(), any()) }
    }

    @Test
    fun turningLocationOffDuringTrackingUnregistersTheListener() {
        val listener = slot<LocationListenerCompat>()
        every {
            LocationManagerCompat.requestLocationUpdates(any(), any<String>(), any<LocationRequestCompat>(), any<Executor>(), capture(listener))
        } just Runs
        val provider = provider()

        provider.startTracking(LocationRequest())
        locationEnabled = false
        listener.captured.onProviderDisabled(LocationManager.GPS_PROVIDER)

        verify(exactly = 1) { LocationManagerCompat.removeUpdates(locationManager, any()) }
    }

    private fun provider() = StandardLocationProvider(context)

    private fun location(elapsedNanos: Long = 0): Location = mockk {
        every { elapsedRealtimeNanos } returns elapsedNanos
    }

    private fun stubCurrentLocation(
        requested: io.mockk.CapturingSlot<String>,
        respond: (Consumer<Location?>) -> Unit,
    ) {
        every {
            LocationManagerCompat.getCurrentLocation(
                any<LocationManager>(),
                capture(requested),
                any<CancellationSignal>(),
                any<Executor>(),
                any<Consumer<Location?>>(),
            )
        } answers {
            respond(arg(4))
        }
    }
}

package dev.jordond.compass.geolocation.mobile.internal

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.location.LocationManager
import android.location.LocationManager.GPS_PROVIDER
import android.location.LocationManager.NETWORK_PROVIDER
import android.os.CancellationSignal
import androidx.core.content.ContextCompat
import androidx.core.location.LocationListenerCompat
import androidx.core.location.LocationManagerCompat
import androidx.core.location.LocationRequestCompat
import androidx.core.util.Consumer
import dev.jordond.compass.Priority
import dev.jordond.compass.exception.NotFoundException
import dev.jordond.compass.geolocation.LocationRequest
import io.mockk.CapturingSlot
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
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
@OptIn(ExperimentalCoroutinesApi::class)
class StandardLocationProviderTest {

    private val executor = mockk<Executor>()
    private val locationManager = mockk<LocationManager>()
    private val context = mockk<Context>()

    private val listener = slot<LocationListenerCompat>()
    private val platformRequest = slot<LocationRequestCompat>()
    private val settingsReceiver = slot<BroadcastReceiver>()

    private var locationEnabled = true
    private var enabledProviders = listOf(GPS_PROVIDER, NETWORK_PROVIDER)

    @BeforeTest
    fun setUp() {
        mockkStatic(ContextCompat::class)
        mockkStatic(LocationManagerCompat::class)
        mockkConstructor(CancellationSignal::class)
        mockkConstructor(IntentFilter::class)

        every { context.applicationContext } returns context
        every { context.getSystemService(Context.LOCATION_SERVICE) } returns locationManager
        every { context.unregisterReceiver(any()) } just Runs
        every { ContextCompat.getMainExecutor(context) } returns executor
        every {
            ContextCompat.registerReceiver(
                context,
                capture(settingsReceiver),
                any(),
                ContextCompat.RECEIVER_NOT_EXPORTED,
            )
        } returns null
        every { anyConstructed<IntentFilter>().addAction(any()) } just Runs
        every { anyConstructed<CancellationSignal>().cancel() } just Runs
        every { LocationManagerCompat.isLocationEnabled(locationManager) } answers { locationEnabled }
        every { locationManager.getProviders(true) } answers { enabledProviders }
        every {
            LocationManagerCompat.requestLocationUpdates(
                locationManager,
                any<String>(),
                capture(platformRequest),
                executor,
                capture(listener),
            )
        } just Runs
        every { LocationManagerCompat.removeUpdates(locationManager, any()) } just Runs
    }

    @AfterTest
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun lastLocationPicksTheMostRecentFixAcrossProviders() = runTest {
        val old = location(elapsedNanos = 100)
        val fresh = location(elapsedNanos = 900)
        every { locationManager.getLastKnownLocation(GPS_PROVIDER) } returns old
        every { locationManager.getLastKnownLocation(NETWORK_PROVIDER) } returns fresh

        assertSame(fresh, provider().lastLocation())
    }

    @Test
    fun lastLocationSkipsProvidersWithoutAFix() = runTest {
        val only = location(elapsedNanos = 5)
        every { locationManager.getLastKnownLocation(GPS_PROVIDER) } returns null
        every { locationManager.getLastKnownLocation(NETWORK_PROVIDER) } returns only

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
        assertEquals(GPS_PROVIDER, requested.captured)
    }

    @Test
    fun currentLocationThrowsNotFoundWhenTheProviderReturnsNothing() = runTest {
        stubCurrentLocation { consumer -> consumer.accept(null) }

        assertFailsWith<NotFoundException> { provider().currentLocation(Priority.HighAccuracy) }
    }

    @Test
    fun cancellingCurrentLocationCancelsThePlatformRequest() = runTest {
        stubCurrentLocation { _ -> }
        val provider = provider()

        val job = launch { provider.currentLocation(Priority.HighAccuracy) }
        runCurrent()
        verify(exactly = 0) { anyConstructed<CancellationSignal>().cancel() }

        job.cancelAndJoin()

        verify(exactly = 1) { anyConstructed<CancellationSignal>().cancel() }
    }

    @Test
    fun startTrackingRegistersOnTheBestProviderOnce() {
        val provider = provider()
        val request = LocationRequest(priority = Priority.Balanced, interval = 2_000L)

        provider.startTracking(request)
        provider.startTracking(request)

        verifyRegistrations(1)
        verifyRegistrations(1, onProvider = NETWORK_PROVIDER)
    }

    @Test
    fun startTrackingBuildsTheRequestFromTheCompassRequest() {
        provider().startTracking(LocationRequest(priority = Priority.HighAccuracy, interval = 3_000L))

        assertEquals(3_000L, platformRequest.captured.intervalMillis)
        assertEquals(3_000L, platformRequest.captured.minUpdateIntervalMillis)
        assertEquals(LocationRequestCompat.QUALITY_HIGH_ACCURACY, platformRequest.captured.quality)
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

        enabledProviders = listOf(GPS_PROVIDER)
        provider.startTracking(LocationRequest())

        verifyRegistrations(1)
    }

    @Test
    fun startTrackingWatchesForLocationSettingChanges() {
        provider().startTracking(LocationRequest())

        assertTrue(settingsReceiver.isCaptured)
        verify(exactly = 1) {
            anyConstructed<IntentFilter>().addAction(LocationManager.MODE_CHANGED_ACTION)
        }
        verify(exactly = 1) {
            anyConstructed<IntentFilter>().addAction(LocationManager.PROVIDERS_CHANGED_ACTION)
        }
    }

    @Test
    fun stopTrackingRemovesTheListenerAndAllowsANewSession() {
        val provider = provider()

        provider.startTracking(LocationRequest())
        val registered = listener.captured
        provider.stopTracking()
        provider.startTracking(LocationRequest())

        verify(exactly = 1) { LocationManagerCompat.removeUpdates(locationManager, registered) }
        verifyRegistrations(2)
    }

    @Test
    fun stopTrackingStopsWatchingTheLocationSettings() {
        val provider = provider()

        provider.startTracking(LocationRequest())
        provider.stopTracking()

        verify(exactly = 1) { context.unregisterReceiver(settingsReceiver.captured) }
    }

    @Test
    fun stopTrackingWithoutASessionDoesNothing() {
        provider().stopTracking()

        verify(exactly = 0) { LocationManagerCompat.removeUpdates(any(), any()) }
        verify(exactly = 0) { context.unregisterReceiver(any()) }
    }

    @Test
    fun locationChangesAreEmittedToLocationUpdates() = runTest {
        val provider = provider()
        val fix = location()

        provider.startTracking(LocationRequest())
        listener.captured.onLocationChanged(fix)

        assertSame(fix, provider.locationUpdates.first())
    }

    @Test
    fun aDisabledProviderMovesTheSessionToTheNextBestOne() {
        val provider = provider()

        provider.startTracking(LocationRequest(priority = Priority.HighAccuracy))
        enabledProviders = listOf(NETWORK_PROVIDER)
        listener.captured.onProviderDisabled(GPS_PROVIDER)

        verifyListenerRemovals(1)
        verifyRegistrations(1, onProvider = GPS_PROVIDER)
        verifyRegistrations(1, onProvider = NETWORK_PROVIDER)
    }

    @Test
    fun aNewlyEnabledBetterProviderTakesOverTheSession() {
        enabledProviders = listOf(NETWORK_PROVIDER)
        val provider = provider()

        provider.startTracking(LocationRequest(priority = Priority.HighAccuracy))
        enabledProviders = listOf(GPS_PROVIDER, NETWORK_PROVIDER)
        listener.captured.onProviderEnabled(GPS_PROVIDER)

        verifyListenerRemovals(1)
        verifyRegistrations(1, onProvider = NETWORK_PROVIDER)
        verifyRegistrations(1, onProvider = GPS_PROVIDER)
    }

    @Test
    fun aRefreshWithTheSameBestProviderDoesNotReRegister() {
        val provider = provider()

        provider.startTracking(LocationRequest(priority = Priority.HighAccuracy))
        listener.captured.onProviderEnabled(NETWORK_PROVIDER)
        broadcastSettingsChange()

        verifyRegistrations(1)
        verifyListenerRemovals(0)
    }

    @Test
    fun turningLocationOffDuringTrackingUnregistersTheListener() {
        val provider = provider()

        provider.startTracking(LocationRequest())
        locationEnabled = false
        broadcastSettingsChange()

        verifyListenerRemovals(1)
        verifyRegistrations(1)
    }

    @Test
    fun trackingResumesWhenLocationIsTurnedBackOn() {
        val provider = provider()

        provider.startTracking(LocationRequest(priority = Priority.HighAccuracy))
        locationEnabled = false
        broadcastSettingsChange()
        locationEnabled = true
        broadcastSettingsChange()

        verifyListenerRemovals(1)
        verifyRegistrations(2, onProvider = GPS_PROVIDER)
    }

    @Test
    fun trackingResumesWhenAProviderComesBackAfterAllWereDisabled() {
        val provider = provider()

        provider.startTracking(LocationRequest(priority = Priority.HighAccuracy))
        enabledProviders = emptyList()
        listener.captured.onProviderDisabled(GPS_PROVIDER)
        enabledProviders = listOf(NETWORK_PROVIDER)
        broadcastSettingsChange()

        verifyRegistrations(1, onProvider = GPS_PROVIDER)
        verifyRegistrations(1, onProvider = NETWORK_PROVIDER)
    }

    private fun provider() = StandardLocationProvider(context)

    private fun location(elapsedNanos: Long = 0): Location = mockk {
        every { elapsedRealtimeNanos } returns elapsedNanos
    }

    private fun broadcastSettingsChange() {
        settingsReceiver.captured.onReceive(context, mockk<Intent>())
    }

    private fun verifyRegistrations(count: Int, onProvider: String? = null) {
        verify(exactly = count) {
            LocationManagerCompat.requestLocationUpdates(
                locationManager,
                onProvider ?: any<String>(),
                any<LocationRequestCompat>(),
                executor,
                any<LocationListenerCompat>(),
            )
        }
    }

    private fun verifyListenerRemovals(count: Int) {
        verify(exactly = count) {
            LocationManagerCompat.removeUpdates(locationManager, listener.captured)
        }
    }

    private fun stubCurrentLocation(
        requested: CapturingSlot<String> = slot(),
        respond: (Consumer<Location?>) -> Unit,
    ) {
        every {
            LocationManagerCompat.getCurrentLocation(
                locationManager,
                capture(requested),
                any<CancellationSignal>(),
                executor,
                any<Consumer<Location?>>(),
            )
        } answers {
            respond(arg(4))
        }
    }
}

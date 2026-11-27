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
import com.google.android.gms.tasks.OnCanceledListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.CancellationToken
import dev.jordond.compass.Priority
import dev.jordond.compass.exception.NotFoundException
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
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

class GmsLocationProviderTest {

    private val context = mockk<Context>(relaxed = true)
    private val fused = mockk<FusedLocationProviderClient>(relaxed = true)
    private val settings = mockk<SettingsClient>()
    private val locationManager = mockk<LocationManager>()
    private val looper = mockk<Looper>()

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
    }

    @AfterTest
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun lastLocationResumesWithTheFusedFix() = runTest {
        val fix = mockk<Location>()
        every { fused.lastLocation } returns task(result = fix)

        assertSame(fix, provider().lastLocation())
    }

    @Test
    fun lastLocationIsNullWhenTheClientHasNone() = runTest {
        every { fused.lastLocation } returns task<Location>(result = null)

        assertNull(provider().lastLocation())
    }

    @Test
    fun lastLocationRethrowsClientFailures() = runTest {
        every { fused.lastLocation } returns task<Location>(failure = IllegalStateException("denied"))

        assertEquals(
            "denied",
            assertFailsWith<IllegalStateException> { provider().lastLocation() }.message,
        )
    }

    @Test
    fun lastLocationSurfacesACancelledTaskAsCancellation() = runTest {
        every { fused.lastLocation } returns task<Location>(cancelled = true)

        assertFailsWith<CancellationException> { provider().lastLocation() }
    }

    @Test
    fun locationEnabledIsTrueWhenTheSettingsCheckPasses() = runTest {
        every { settings.checkLocationSettings(any()) } returns
            completedTask(mockk<LocationSettingsResponse>())

        assertTrue(provider().locationEnabled())
    }

    @Test
    fun locationEnabledFallsBackToThePlatformSettingWhenTheCheckFails() = runTest {
        every { settings.checkLocationSettings(any()) } returns
            completedTask<LocationSettingsResponse>(failure = RuntimeException("resolution required"))
        every { LocationManagerCompat.isLocationEnabled(locationManager) } returns true

        assertTrue(provider().locationEnabled())
    }

    @Test
    fun locationEnabledIsFalseWhenBothTheCheckAndThePlatformSayOff() = runTest {
        every { settings.checkLocationSettings(any()) } returns
            completedTask<LocationSettingsResponse>(failure = RuntimeException("off"))
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
                completedTask(fix)

            assertSame(fix, provider.currentLocation(priority))
        }
    }

    @Test
    fun currentLocationThrowsNotFoundWhenTheClientReturnsNull() = runTest {
        every { fused.getCurrentLocation(any<Int>(), any<CancellationToken>()) } returns
            completedTask<Location>(result = null)

        assertFailsWith<NotFoundException> { provider().currentLocation(Priority.Balanced) }
    }

    @Test
    fun startTrackingRegistersOnceWithAnIntervalMatchedRequest() {
        val request = slot<LocationRequest>()
        every { fused.requestLocationUpdates(capture(request), any<LocationCallback>(), looper) } returns
            mockk(relaxed = true)
        val provider = provider()
        val compassRequest = CompassLocationRequest(priority = Priority.HighAccuracy, interval = 4_000L)

        provider.startTracking(compassRequest)
        provider.startTracking(compassRequest)

        verify(exactly = 1) { fused.requestLocationUpdates(any<LocationRequest>(), any<LocationCallback>(), looper) }
        assertEquals(GmsPriority.PRIORITY_HIGH_ACCURACY, request.captured.priority)
        assertEquals(4_000L, request.captured.intervalMillis)
        assertEquals(4_000L, request.captured.minUpdateIntervalMillis)
    }

    @Test
    fun stopTrackingRemovesTheCallbackAndAllowsANewSession() {
        val registered = slot<LocationCallback>()
        every { fused.requestLocationUpdates(any<LocationRequest>(), capture(registered), looper) } returns
            mockk(relaxed = true)
        val provider = provider()

        provider.startTracking(CompassLocationRequest())
        provider.stopTracking()
        provider.startTracking(CompassLocationRequest())

        verify(exactly = 1) { fused.removeLocationUpdates(any<LocationCallback>()) }
        verify(exactly = 2) { fused.requestLocationUpdates(any<LocationRequest>(), any<LocationCallback>(), looper) }
    }

    @Test
    fun stopTrackingWithoutASessionDoesNothing() {
        provider().stopTracking()

        verify(exactly = 0) { fused.removeLocationUpdates(any<LocationCallback>()) }
    }

    @Test
    fun locationResultsAreEmittedToLocationUpdates() = runTest {
        val registered = slot<LocationCallback>()
        every { fused.requestLocationUpdates(any<LocationRequest>(), capture(registered), looper) } returns
            mockk(relaxed = true)
        val provider = provider()
        val received = mutableListOf<Location>()
        val job = launch { provider.locationUpdates.collect { received += it } }

        provider.startTracking(CompassLocationRequest())
        val fix = mockk<Location>()
        registered.captured.onLocationResult(mockk<LocationResult> { every { lastLocation } returns fix })
        registered.captured.onLocationResult(mockk<LocationResult> { every { lastLocation } returns null })
        testScheduler.advanceUntilIdle()

        assertEquals(listOf(fix), received)
        job.cancel()
    }

    private fun provider() = GmsLocationProvider(context)

    private fun <T> completedTask(
        result: T? = null,
        failure: Exception? = null,
    ): Task<T> = mockk {
        every { isComplete } returns true
        every { isCanceled } returns false
        every { exception } returns failure
        every { getResult() } returns result
    }

    private fun <T> task(
        result: T? = null,
        failure: Exception? = null,
        cancelled: Boolean = false,
    ): Task<T> {
        val task = mockk<Task<T>>()
        every { task.addOnSuccessListener(any<OnSuccessListener<in T>>()) } answers {
            if (failure == null && !cancelled) firstArg<OnSuccessListener<T?>>().onSuccess(result)
            task
        }
        every { task.addOnCanceledListener(any<OnCanceledListener>()) } answers {
            if (cancelled) firstArg<OnCanceledListener>().onCanceled()
            task
        }
        every { task.addOnFailureListener(any<OnFailureListener>()) } answers {
            if (failure != null) firstArg<OnFailureListener>().onFailure(failure)
            task
        }
        return task
    }
}

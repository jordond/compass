package dev.jordond.compass.geolocation.mobile.gms.internal

import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import androidx.core.location.LocationManagerCompat
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority.PRIORITY_BALANCED_POWER_ACCURACY
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.Priority.PRIORITY_LOW_POWER
import com.google.android.gms.location.Priority.PRIORITY_PASSIVE
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.CancellationTokenSource
import dev.jordond.compass.Priority
import dev.jordond.compass.exception.NotFoundException
import dev.jordond.compass.geolocation.mobile.PlatformLocationProvider
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import dev.jordond.compass.geolocation.LocationRequest as CompassLocationRequest

/**
 * A [PlatformLocationProvider] backed by the Google Play Services fused location provider.
 */
internal class GmsLocationProvider(
    context: Context,
) : PlatformLocationProvider {

    private val context: Context = context.applicationContext

    private val _locationUpdates = MutableSharedFlow<Location>(
        replay = 1,
        extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    override val locationUpdates: Flow<Location> = _locationUpdates

    private var locationCallback: LocationCallback? = null

    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(this.context)
    }

    private val settingsClient: SettingsClient by lazy {
        LocationServices.getSettingsClient(this.context)
    }

    override suspend fun lastLocation(): Location? = suspendCancellableCoroutine { continuation ->
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                continuation.resume(location)
            }.addOnCanceledListener {
                continuation.resumeWithException(CancellationException())
            }.addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
    }

    override suspend fun locationEnabled(): Boolean {
        val request = LocationSettingsRequest.Builder().addAllLocationRequests(
            listOf(
                LocationRequest.Builder(PRIORITY_HIGH_ACCURACY, 1000).build(),
                LocationRequest.Builder(PRIORITY_BALANCED_POWER_ACCURACY, 1000).build(),
                LocationRequest.Builder(PRIORITY_PASSIVE, 1000).build(),
                LocationRequest.Builder(PRIORITY_LOW_POWER, 1000).build(),
            )
        ).build()

        val result = runCatching {
            settingsClient.checkLocationSettings(request).await()
        }.isSuccess

        // checkLocationSettings fails when the settings do not satisfy every request above, which
        // is stricter than "can this device locate itself at all". Fall back to the platform
        // answer so a device with location on is never reported as unavailable.
        return result || legacyLocationEnabled()
    }

    override suspend fun currentLocation(priority: Priority): Location {
        val cancellation = CancellationTokenSource()

        @OptIn(ExperimentalCoroutinesApi::class)
        val location: Location? = fusedLocationClient
            .getCurrentLocation(priority.toGmsPriority(), cancellation.token)
            .await(cancellation)

        if (location == null) {
            throw NotFoundException()
        }

        return location
    }

    override fun startTracking(request: CompassLocationRequest) {
        if (locationCallback != null) return

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location -> _locationUpdates.tryEmit(location) }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            request.toGmsLocationRequest(),
            callback,
            Looper.getMainLooper(),
        )
        locationCallback = callback
    }

    override fun stopTracking() {
        locationCallback?.let { callback ->
            fusedLocationClient.removeLocationUpdates(callback)
            locationCallback = null
        }
    }

    private fun legacyLocationEnabled(): Boolean {
        val locationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return LocationManagerCompat.isLocationEnabled(locationManager)
    }

    private fun Priority.toGmsPriority(): Int = when (this) {
        Priority.Balanced -> PRIORITY_BALANCED_POWER_ACCURACY
        Priority.HighAccuracy -> PRIORITY_HIGH_ACCURACY
        Priority.LowPower -> PRIORITY_LOW_POWER
        Priority.Passive -> PRIORITY_PASSIVE
    }

    private fun CompassLocationRequest.toGmsLocationRequest(): LocationRequest =
        LocationRequest
            .Builder(priority.toGmsPriority(), interval)
            .setMinUpdateIntervalMillis(interval)
            .setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            .build()
}

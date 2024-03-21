package dev.jordond.compass.geolocation.mobile

import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

internal class LocationManager(
    private val context: Context,
) {

    private val _locationUpdates = MutableSharedFlow<LocationResult>(
        replay = 1,
        extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    internal val locationUpdates: Flow<LocationResult> = _locationUpdates

    private var locationCallback: LocationCallback? = null

    private val fusedLocationClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    suspend fun lastLocation(): Location? = suspendCoroutine { continuation ->
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location -> continuation.resume(location) }
            .addOnFailureListener { exception -> continuation.resumeWithException(exception) }
    }

    suspend fun currentLocation(priority: Int): Location {
        return suspendCancellableCoroutine { continuation ->
            val cancellation = CancellationTokenSource()

            fusedLocationClient
                .getCurrentLocation(priority, cancellation.token)
                .addOnSuccessListener { location -> continuation.resume(location) }
                .addOnFailureListener { exception -> continuation.resumeWithException(exception) }

            continuation.invokeOnCancellation {
                cancellation.cancel()
            }
        }
    }

    fun startTracking(request: LocationRequest): Flow<LocationResult> {
        if (locationCallback != null) return _locationUpdates

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                _locationUpdates.tryEmit(result)
            }
        }

        fusedLocationClient.requestLocationUpdates(request, callback, Looper.getMainLooper())
        locationCallback = callback

        return _locationUpdates
    }

    fun stopTracking() {
        locationCallback?.let { callback ->
            fusedLocationClient.removeLocationUpdates(callback)
            locationCallback = null
        }
    }
}

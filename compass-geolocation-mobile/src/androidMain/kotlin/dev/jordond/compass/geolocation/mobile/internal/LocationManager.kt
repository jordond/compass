package dev.jordond.compass.geolocation.mobile.internal

import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import dev.jordond.compass.exception.NotFoundException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.tasks.await

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

    fun locationEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun currentLocation(priority: Int): Location {
        val cancellation = CancellationTokenSource()

        val location: Location? = fusedLocationClient
            .getCurrentLocation(priority, cancellation.token)
            .await(cancellation)

        if (location == null) {
            throw NotFoundException()
        }
        return location
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

@file:Suppress("ThrowableNotThrown")

package dev.jordond.compass.geolocation.mobile

import co.touchlab.kermit.Logger
import dev.jordond.compass.Location
import dev.jordond.compass.geolocation.LocationRequest
import dev.jordond.compass.geolocation.Priority
import dev.jordond.compass.geolocation.exception.GeolocationException
import dev.jordond.compass.geolocation.mobile.internal.LocationTracker
import dev.jordond.compass.geolocation.mobile.internal.toIosPriority
import dev.jordond.compass.geolocation.mobile.internal.toModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLLocationAccuracyKilometer
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal actual fun createLocator(handlePermissions: Boolean): MobileLocator {
    return IosLocator(handlePermissions)
}

internal class IosLocator(
    private val handlePermissions: Boolean,
) : MobileLocator {

    private val _locationUpdates = MutableSharedFlow<Location>(
        replay = 1,
        extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    internal val locationUpdates: Flow<Location> = _locationUpdates

    private var tracker = LocationTracker(
        onLocationUpdate = { location -> _locationUpdates.tryEmit(location.toModel()) },
        onLocationError = { error ->
            Logger.e("Error getting location error: ${error.localizedDescription}")
        }
    )
    private var locationManager: CLLocationManager? = null

    override fun isAvailable(): Boolean {
        val locationManager = CLLocationManager()
        return when (locationManager.authorizationStatus()) {
            kCLAuthorizationStatusAuthorizedWhenInUse,
            kCLAuthorizationStatusAuthorizedAlways,
            -> true
            else -> false
        }
    }

    override suspend fun last(): Location? {
        return suspendCancellableCoroutine { continuation ->
            val callback = LocationTracker(
                onLocationUpdate = { location -> continuation.resume(location.toModel()) },
                onLocationError = { error ->
                    continuation.resumeWithException(
                        GeolocationException(error.localizedDescription)
                    )
                }
            )

            CLLocationManager().apply {
                desiredAccuracy = kCLLocationAccuracyKilometer
                delegate = callback
                requestLocation()
            }
        }
    }

    override suspend fun current(priority: Priority): Location {
        return suspendCancellableCoroutine { continuation ->
            val locationManager = CLLocationManager().apply {
                desiredAccuracy = priority.toIosPriority
                delegate = LocationTracker(
                    onLocationUpdate = { location -> continuation.resume(location.toModel()) },
                    onLocationError = { error ->
                        continuation.resumeWithException(
                            GeolocationException(error.localizedDescription)
                        )
                    }
                )
                startUpdatingLocation()
            }

            continuation.invokeOnCancellation {
                locationManager.stopUpdatingLocation()
            }
        }
    }

    override fun track(request: LocationRequest): Flow<Location> {
        if (locationManager != null) return locationUpdates

        locationManager = CLLocationManager().apply {
            desiredAccuracy = request.priority.toIosPriority
            delegate = tracker
            startUpdatingLocation()
        }
        return locationUpdates
    }

    override fun stopTracking() {
        locationManager?.stopUpdatingLocation()
        locationManager = null
    }
}
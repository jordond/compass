package dev.jordond.compass.geolocation.mobile

import android.content.Context
import dev.jordond.compass.Location
import dev.jordond.compass.Priority
import dev.jordond.compass.geolocation.LocationRequest
import dev.jordond.compass.geolocation.mobile.internal.LocationManager
import dev.jordond.compass.geolocation.mobile.internal.toAndroidLocationRequest
import dev.jordond.compass.geolocation.mobile.internal.toAndroidPriority
import dev.jordond.compass.geolocation.mobile.internal.toModel
import dev.jordond.compass.permissions.LocationPermissionController
import dev.jordond.compass.permissions.PermissionState
import dev.jordond.compass.permissions.throwOnError
import dev.jordond.compass.tools.ContextProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

/**
 * Creates a new [MobileLocator] instance for Android.
 */
internal actual fun createLocator(
    permissionController: LocationPermissionController,
): MobileLocator {
    return AndroidLocator(ContextProvider.getInstance().context, permissionController)
}

internal class AndroidLocator(
    private val context: Context,
    private val permissionController: LocationPermissionController,
    private val locationManager: LocationManager = LocationManager(context),
) : MobileLocator {

    override val locationUpdates: Flow<Location> = locationManager.locationUpdates
        .mapNotNull { result -> result.lastLocation?.toModel() }

    override suspend fun lastLocation(priority: Priority): Location? {
        requirePermission(priority)
        return locationManager.lastLocation()?.toModel()
    }

    override suspend fun isAvailable(): Boolean {
        return locationManager.locationEnabled()
    }

    override fun hasPermission(): Boolean {
        return permissionController.hasPermission()
    }

    override suspend fun current(priority: Priority): Location {
        requirePermission(priority)
        return locationManager.currentLocation(priority.toAndroidPriority).toModel()
    }

    override suspend fun track(request: LocationRequest): Flow<Location> {
        requirePermission(request.priority)
        locationManager.startTracking(request.toAndroidLocationRequest())

        return locationUpdates
    }

    override fun stopTracking() {
        locationManager.stopTracking()
    }

    private suspend fun requirePermission(priority: Priority) {
        val state = permissionController.requirePermissionFor(priority)
        if (state != PermissionState.Granted) {
            state.throwOnError()
        }
    }
}
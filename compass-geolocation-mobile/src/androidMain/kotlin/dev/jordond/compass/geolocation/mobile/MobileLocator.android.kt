package dev.jordond.compass.geolocation.mobile

import android.content.Context
import dev.jordond.compass.Location
import dev.jordond.compass.geolocation.LocationRequest
import dev.jordond.compass.geolocation.Priority
import dev.jordond.compass.geolocation.mobile.internal.LocationManager
import dev.jordond.compass.geolocation.mobile.internal.PermissionController
import dev.jordond.compass.geolocation.mobile.internal.toAndroidLocationRequest
import dev.jordond.compass.geolocation.mobile.internal.toAndroidPriority
import dev.jordond.compass.geolocation.mobile.internal.toModel
import dev.jordond.compass.tools.ContextProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull

internal actual fun createLocator(handlePermissions: Boolean): MobileLocator {
    return AndroidLocator(ContextProvider.getInstance().context, handlePermissions)
}

internal class AndroidLocator(
    private val context: Context,
    handlePermissions: Boolean,
    private val locationManager: LocationManager = LocationManager(context),
    private val permissionController: PermissionController =
        PermissionController(context, handlePermissions),
) : MobileLocator {

    override fun isAvailable(): Boolean {
        return locationManager.locationEnabled()
    }

    override fun hasPermission(): Boolean {
        return permissionController.hasAny()
    }

    override suspend fun last(): Location? {
        requirePermission(Priority.Passive)
        return locationManager.lastLocation()?.toModel()
    }

    override suspend fun current(priority: Priority): Location {
        requirePermission(priority)
        return locationManager.currentLocation(priority.toAndroidPriority).toModel()
    }

    override fun track(request: LocationRequest): Flow<Location> = flow {
        val permissionResult = permissionController.requirePermissionFor(request.priority)
        if (permissionResult !is PermissionResult.Granted) {
            permissionResult.throwOnError()
        } else {
            locationManager.startTracking(request.toAndroidLocationRequest())
            locationManager.locationUpdates
                .mapNotNull { result ->
                    result.lastLocation?.toModel()
                }
                .collect { location -> emit(location) }
        }
    }

    override fun stopTracking() {
        locationManager.stopTracking()
    }

    private suspend fun requirePermission(priority: Priority) {
        val permissionResult = permissionController.requirePermissionFor(priority)
        if (permissionResult !is PermissionResult.Granted) {
            permissionResult.throwOnError()
        }
    }
}
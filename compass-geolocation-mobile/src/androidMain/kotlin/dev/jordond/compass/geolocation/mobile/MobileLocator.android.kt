package dev.jordond.compass.geolocation.mobile

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import dev.jordond.compass.Location
import dev.jordond.compass.geolocation.LocationRequest
import dev.jordond.compass.geolocation.Priority
import dev.jordond.compass.geolocation.mobile.internal.toAndroidLocationRequest
import dev.jordond.compass.geolocation.mobile.internal.toAndroidPriority
import dev.jordond.compass.geolocation.mobile.internal.toModel
import dev.jordond.compass.tools.ContextProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

internal actual fun createLocator(): MobileLocator {
    return AndroidLocator(ContextProvider.getInstance().context)
}

internal class AndroidLocator(
    private val context: Context,
    private val locationManager: LocationManager = LocationManager(context),
) : MobileLocator {

    override fun isAvailable(): Boolean {
        return context.hasAnyPermission()
    }

    override suspend fun last(): Location? {
        return locationManager.lastLocation()?.toModel()
    }

    override suspend fun current(priority: Priority): Location {
        return locationManager.currentLocation(priority.toAndroidPriority).toModel()
    }

    override fun track(request: LocationRequest): Flow<Location> {
        locationManager.startTracking(request.toAndroidLocationRequest())
        return locationManager.locationUpdates.mapNotNull { result ->
            result.lastLocation?.toModel()
        }
    }

    override fun stopTracking() {
        locationManager.stopTracking()
    }
}

private fun Context.hasAnyPermission(): Boolean {
    return listOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
    ).any { hasPermission(it) }
}

private fun Context.hasPermission(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}
package dev.jordond.compass.permissions

import dev.jordond.compass.Priority
import dev.jordond.compass.permissions.mobile.internal.LocationPermissionManagerDelegate
import dev.jordond.compass.permissions.mobile.internal.toPermissionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

internal actual fun createPermissionController(): LocationPermissionController {
    return IosLocationPermissionController(LocationPermissionManagerDelegate())
}

internal class IosLocationPermissionController(
    private val locationDelegate: LocationPermissionManagerDelegate,
) : LocationPermissionController {

    // Seeded by `monitorPermission` below, which reports the current status as soon as it gets onto
    // the main thread. `requirePermissionFor` reads a fresh status rather than this flow, so the
    // permission gate stays correct even before the first report lands.
    private val _permissionsStatus = MutableStateFlow(PermissionState.NotDetermined)

    init {
        locationDelegate.monitorPermission { permissionStatus ->
            _permissionsStatus.update { permissionStatus.toPermissionState }
        }
    }

    override fun hasPermission(): Boolean {
        return _permissionsStatus.value == PermissionState.Granted
    }

    override suspend fun requirePermissionFor(priority: Priority): PermissionState {
        val currentState = locationDelegate.currentPermissionStatus().toPermissionState
        return when {
            currentState == PermissionState.Granted ||
                currentState == PermissionState.DeniedForever -> currentState
            else -> locationDelegate.requestPermission().toPermissionState
        }
    }
}

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

    // Seeded up front rather than left to the first report from `monitorPermission`, which arrives
    // a main thread hop later. `hasPermission` cannot suspend to wait for that, so seeding late
    // would have it answer `false` for an app that already holds the permission.
    private val _permissionsStatus = MutableStateFlow(
        value = locationDelegate.currentPermissionStatus().toPermissionState,
    )

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

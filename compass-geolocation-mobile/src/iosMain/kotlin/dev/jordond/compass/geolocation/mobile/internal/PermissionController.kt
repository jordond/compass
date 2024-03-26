package dev.jordond.compass.geolocation.mobile.internal

import dev.jordond.compass.geolocation.PermissionState
import dev.jordond.compass.geolocation.exception.PermissionMissingException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal class PermissionController(
    private val handlePermissions: Boolean,
    private val locationDelegate: LocationManagerDelegate,
) {

    private val mutex: Mutex = Mutex()

    private val _permissionsStatus = MutableStateFlow(
        value = locationDelegate.currentPermissionStatus().toPermissionState,
    )
    val permissionsState: Flow<PermissionState> = _permissionsStatus

    init {
        locationDelegate.monitorPermission { permissionStatus ->
            _permissionsStatus.update { permissionStatus.toPermissionState }
        }
    }

    fun hasPermission(): Boolean {
        return _permissionsStatus.value == PermissionState.Granted
    }

    suspend fun requirePermission(): PermissionState {
        val currentState = locationDelegate.currentPermissionStatus().toPermissionState
        return when {
            currentState == PermissionState.Granted ||
                currentState == PermissionState.DeniedForever -> currentState
            !handlePermissions -> throw PermissionMissingException("When in use location")
            else -> mutex.withLock {
                val result = suspendCoroutine { continuation ->
                    locationDelegate.requestPermission { continuation.resume(it) }
                }

                result.toPermissionState
            }
        }
    }
}
package dev.jordond.compass.geolocation.mobile.internal

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import dev.jordond.compass.geolocation.PermissionState
import dev.jordond.compass.geolocation.Priority
import dev.jordond.compass.geolocation.exception.PermissionMissingException
import dev.jordond.compass.geolocation.mobile.internal.activity.ActivityProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal class PermissionController(
    private val context: Context,
    private val handlePermissions: Boolean,
    private val activityProvider: ActivityProvider = ActivityProvider.getInstance(),
) {

    private val _permissionState = MutableStateFlow(PermissionState.NotDetermined)

    private val mutex: Mutex = Mutex()

    fun hasAny(): Boolean {
        return context.hasAnyPermission()
    }

    suspend fun requirePermissionFor(priority: Priority): PermissionState {
        val permissions = permissionsFor(priority).filter { !context.hasPermission(it) }
        if (permissions.isEmpty() || permissions.hasPermissions()) return PermissionState.Granted
        if (!handlePermissions) {
            throw PermissionMissingException(permissions.joinToString(", "))
        }

        return mutex.withLock {
            suspendCoroutine { continuation ->
                activityProvider.permissionRequester.request(permissions) { result ->
                    continuation.resume(result)
                }
            }
        }
    }

    private fun permissionsFor(priority: Priority): List<String> {
        return when (priority) {
            Priority.HighAccuracy -> listOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
            Priority.Balanced,
            Priority.LowPower,
            Priority.Passive,
            -> listOf(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
    }

    private fun List<String>.hasPermissions(): Boolean {
        return all { context.hasPermission(it) }
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
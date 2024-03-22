package dev.jordond.compass.geolocation.mobile

import dev.jordond.compass.geolocation.exception.PermissionDeniedException
import dev.jordond.compass.geolocation.exception.PermissionDeniedForeverException
import dev.jordond.compass.geolocation.exception.PermissionRequestCancelledException

public sealed interface PermissionResult {
    public data object Granted : PermissionResult
    public data class Cancelled(public val permission: Permission) : PermissionResult
    public data class Denied(public val permission: Permission) : PermissionResult
    public data class DeniedForever(public val permission: Permission) : PermissionResult
}

internal fun PermissionResult.throwOnError() {
    when (this) {
        is PermissionResult.Granted -> Unit
        is PermissionResult.Cancelled -> throw PermissionRequestCancelledException(permission.name)
        is PermissionResult.Denied -> throw PermissionDeniedException(permission.name)
        is PermissionResult.DeniedForever -> throw PermissionDeniedForeverException(permission.name)
    }
}
package dev.jordond.compass.geolocation.mobile.internal

import dev.jordond.compass.geolocation.PermissionState
import dev.jordond.compass.geolocation.exception.PermissionDeniedException
import dev.jordond.compass.geolocation.exception.PermissionDeniedForeverException

/**
 * Throws an exception if the permission state is not [PermissionState.Granted] or
 * [PermissionState.NotDetermined].
 */
internal fun PermissionState.throwOnError() {
    when (this) {
        PermissionState.NotDetermined,
        PermissionState.Granted,
        -> Unit
        PermissionState.Denied -> throw PermissionDeniedException()
        PermissionState.DeniedForever -> throw PermissionDeniedForeverException()
    }
}
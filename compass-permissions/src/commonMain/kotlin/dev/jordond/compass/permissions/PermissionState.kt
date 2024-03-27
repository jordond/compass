package dev.jordond.compass.permissions

import dev.jordond.compass.permissions.exception.PermissionDeniedException
import dev.jordond.compass.permissions.exception.PermissionDeniedForeverException

/**
 * Describes the state of the location permission.
 */
public enum class PermissionState {

    /**
     * The permission has not been determined.
     */
    NotDetermined,

    /**
     * The permission has been granted.
     */
    Granted,

    /**
     * The permission has been denied.
     */
    Denied,

    /**
     * The permission has been denied forever.
     *
     * This means the user has denied the permission and selected "Don't ask again". The user must
     * manually enable the permission in the settings.
     */
    DeniedForever,
}

/**
 * Throws an exception if the permission state is not [PermissionState.Granted] or
 * [PermissionState.NotDetermined].
 */
public fun PermissionState.throwOnError() {
    when (this) {
        PermissionState.NotDetermined,
        PermissionState.Granted,
        -> Unit
        PermissionState.Denied -> throw PermissionDeniedException()
        PermissionState.DeniedForever -> throw PermissionDeniedForeverException()
    }
}
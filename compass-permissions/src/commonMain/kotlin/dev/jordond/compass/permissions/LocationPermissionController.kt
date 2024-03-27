package dev.jordond.compass.permissions

import dev.jordond.compass.Priority
import dev.jordond.compass.permissions.exception.PermissionDeniedException
import dev.jordond.compass.permissions.exception.PermissionDeniedForeverException

/**
 * Defines a controller for managing location permissions.
 */
public interface LocationPermissionController {

    /**
     * Checks if the app has the necessary permissions to access the device's location.
     *
     * @return `true` if the app has the necessary permissions, `false` otherwise.
     */
    public fun hasPermission(): Boolean

    /**
     * Requests the necessary permissions to access the device's location.
     *
     * @return The state of the permission after the request.
     */
    public suspend fun requirePermissionFor(priority: Priority): PermissionState

    /**
     * Requests the necessary permissions to access the device's location, and throws an exception
     * if the permission is denied.
     *
     * @throws PermissionDeniedException If the permission is denied.
     * @throws PermissionDeniedForeverException If the permission is missing.
     */
    public suspend fun requirePermissionForOrThrow(priority: Priority) {
        requirePermissionFor(priority).also { it.throwOnError() }
    }

    public companion object
}

package dev.jordond.compass.permissions

import dev.jordond.compass.Priority
import kotlinx.coroutines.flow.Flow

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

    public companion object
}

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
     * How precise the locations the app is currently allowed to read are.
     *
     * [PermissionState.Granted] does not imply [LocationAccuracy.Full]. The user can grant the
     * location permission while withholding precision, on Android by answering "Approximate" and on
     * iOS by turning Precise Location off, and a request for a finer accuracy is then clamped by
     * the system. Check this before relying on a fix being accurate to within a few hundred metres.
     *
     * Reads the current state, it never prompts.
     *
     * @return [LocationAccuracy.Unknown] when the app holds no location permission at all.
     */
    public fun grantedAccuracy(): LocationAccuracy = LocationAccuracy.Unknown

    /**
     * Requests the necessary permissions to access the device's location.
     *
     * What `priority` does differs by platform, because the platforms let an app ask for precision
     * at different times.
     *
     * On **Android** it selects which permissions are requested.
     * [dev.jordond.compass.Priority.HighAccuracy] requests `ACCESS_FINE_LOCATION` alongside
     * `ACCESS_COARSE_LOCATION`, every other priority requests `ACCESS_COARSE_LOCATION` only.
     * Answering the fine request with "Approximate" leaves the fine permission ungranted, so
     * [PermissionState.Granted] for [dev.jordond.compass.Priority.HighAccuracy] does mean the app
     * holds precise location.
     *
     * On **iOS** it is ignored. `CLLocationManager` offers no way to ask for precision up front,
     * the user chooses Precise Location on or off inside the one system prompt, so there is nothing
     * to select between. [PermissionState.Granted] therefore only means the app may read a
     * location, at whatever precision the user allowed. Use [grantedAccuracy] to find out which
     * that is.
     *
     * @param priority The accuracy the caller intends to request locations at. Selects the
     * requested permissions on Android, ignored on iOS, see above.
     * @return The state of the permission after the request.
     */
    public suspend fun requirePermissionFor(priority: Priority): PermissionState

    /**
     * Requests the necessary permissions to access the device's location, and throws an exception
     * if the permission is denied.
     *
     * @param priority The accuracy the caller intends to request locations at. See
     * [requirePermissionFor] for what this does on each platform, in particular that iOS ignores
     * it and can grant a reduced accuracy permission without throwing.
     * @throws PermissionDeniedException If the permission is denied.
     * @throws PermissionDeniedForeverException If the permission is missing.
     */
    public suspend fun requirePermissionForOrThrow(priority: Priority) {
        requirePermissionFor(priority).also { it.throwOnError() }
    }

    public companion object
}

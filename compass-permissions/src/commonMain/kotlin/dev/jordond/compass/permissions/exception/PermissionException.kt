package dev.jordond.compass.permissions.exception

/**
 * Defines an exception that is related to Geolocation permissions.
 */
public sealed class PermissionException(message: String) : Throwable(message)

/**
 * The user denied the permission request.
 *
 * @param permission The permission that was denied.
 */
public class PermissionDeniedException(
    permission: String = "Location",
) : PermissionException("Permission $permission was denied")

/**
 * The user denied the permission request and selected "Don't ask again".
 *
 * @param permission The permission that was denied.
 */
public class PermissionDeniedForeverException(
    permission: String = "Location",
) : PermissionException("Permission $permission was denied permanently")

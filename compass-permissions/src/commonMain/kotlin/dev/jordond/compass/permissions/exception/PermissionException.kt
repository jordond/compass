package dev.jordond.compass.permissions.exception

/**
 * Defines an exception that is related to Geolocation permissions.
 */
public sealed class PermissionException(message: String) : Throwable(message)

/**
 * An error occurred while requesting a permission.
 *
 * @param message The error message.
 * @param permission The permission that was requested.
 */
public class PermissionRequestException(
    message: String,
    permission: String = "Location",
) : PermissionException("Failed to request permission: $permission, because $message")

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

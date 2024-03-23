package dev.jordond.compass.geolocation.exception

/**
 * Defines an exception that is related to Geolocation permissions.
 */
public sealed interface PermissionException

/**
 * Permissions is missing and we will not attempt to request the permission.
 */
public class PermissionMissingException(permission: String) : Throwable(
    "Permission $permission is required to use the Geolocation API"
), PermissionException

/**
 * An error occurred while requesting a permission.
 *
 * @param message The error message.
 * @param permission The permission that was requested.
 */
public class PermissionRequestException(
    message: String,
    permission: String = "Location",
) : Throwable("Failed to request permission: $permission, because $message"), PermissionException

/**
 * The user denied the permission request.
 *
 * @param permission The permission that was denied.
 */
public class PermissionDeniedException(
    permission: String = "Location",
) : Throwable("Permission $permission was denied"), PermissionException

/**
 * The user denied the permission request and selected "Don't ask again".
 *
 * @param permission The permission that was denied.
 */
public class PermissionDeniedForeverException(
    permission: String = "Location",
) : Throwable("Permission $permission was denied permanently"), PermissionException

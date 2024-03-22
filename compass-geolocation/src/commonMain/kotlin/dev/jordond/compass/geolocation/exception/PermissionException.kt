package dev.jordond.compass.geolocation.exception

public sealed interface PermissionException

public class PermissionMissingException(permission: String) : Throwable(
    "Permission $permission is required to use the Geolocation API"
), PermissionException

public class PermissionRequestException(permission: String, message: String) : Throwable(
    "Failed to request permission: $permission, because $message"
), PermissionException

public class PermissionRequestCancelledException(permission: String) : Throwable(
    "Permission request for $permission was cancelled"
), PermissionException

public class PermissionDeniedException(permission: String) : Throwable(
    "Permission $permission was denied"
), PermissionException

public class PermissionDeniedForeverException(permission: String) : Throwable(
    "Permission $permission was denied permanently"
), PermissionException
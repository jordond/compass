package dev.jordond.compass.permissions

/**
 * Creates a new [LocationPermissionController] for Android and iOS.
 *
 * @return A new [LocationPermissionController].
 */
public fun LocationPermissionController(): LocationPermissionController {
    return createPermissionController()
}

/**
 * Creates a new [LocationPermissionController] for Android and iOS.
 *
 * @return A new [LocationPermissionController].
 */
@Suppress("FunctionName")
public fun MobileLocationPermissionController(): LocationPermissionController {
    return LocationPermissionController()
}

/**
 * Creates a new [LocationPermissionController] for Android and iOS.
 *
 * @return A new [LocationPermissionController].
 */
public fun LocationPermissionController.Companion.mobile(): LocationPermissionController {
    return LocationPermissionController()
}

internal expect fun createPermissionController(): LocationPermissionController
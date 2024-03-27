package dev.jordond.compass.permissions.mobile

import dev.jordond.compass.permissions.LocationPermissionController

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
public fun LocationPermissionController.Companion.mobile(): LocationPermissionController {
    return LocationPermissionController()
}

internal expect fun createPermissionController(): LocationPermissionController
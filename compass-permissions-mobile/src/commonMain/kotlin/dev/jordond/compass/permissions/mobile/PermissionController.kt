package dev.jordond.compass.permissions.mobile

import dev.jordond.compass.permissions.LocationPermissionController

public fun LocationPermissionController(): LocationPermissionController {
    return createPermissionController()
}

public fun LocationPermissionController.Companion.mobile(): LocationPermissionController {
    return LocationPermissionController()
}

internal expect fun createPermissionController(): LocationPermissionController
package dev.jordond.compass.permissions.mobile.internal

import dev.jordond.compass.permissions.PermissionState
import platform.CoreLocation.CLAuthorizationStatus
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLAuthorizationStatusDenied
import platform.CoreLocation.kCLAuthorizationStatusNotDetermined
import platform.CoreLocation.kCLAuthorizationStatusRestricted

internal val CLAuthorizationStatus.toPermissionState: PermissionState
    get() = when (this) {
        kCLAuthorizationStatusAuthorizedAlways,
        kCLAuthorizationStatusAuthorizedWhenInUse,
        -> PermissionState.Granted
        kCLAuthorizationStatusNotDetermined -> PermissionState.NotDetermined
        kCLAuthorizationStatusDenied -> PermissionState.DeniedForever
        // Location services are restricted by parental controls or an MDM profile. The user cannot
        // lift that themselves, so it is terminal in the same way an explicit denial is.
        kCLAuthorizationStatusRestricted -> PermissionState.DeniedForever
        // CLAuthorizationStatus is an Objective-C enum, so a value Apple adds later lands here.
        // Fail closed instead of crashing, on the one state that will neither re-prompt nor
        // suspend waiting for an answer that is never coming.
        else -> PermissionState.DeniedForever
    }

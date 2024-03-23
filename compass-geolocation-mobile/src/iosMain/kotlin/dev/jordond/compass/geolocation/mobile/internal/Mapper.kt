package dev.jordond.compass.geolocation.mobile.internal

import dev.jordond.compass.Altitude
import dev.jordond.compass.Azimuth
import dev.jordond.compass.Coordinates
import dev.jordond.compass.Location
import dev.jordond.compass.Speed
import dev.jordond.compass.geolocation.PermissionState
import dev.jordond.compass.geolocation.Priority
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreLocation.CLAuthorizationStatus
import platform.CoreLocation.CLLocation
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLAuthorizationStatusDenied
import platform.CoreLocation.kCLAuthorizationStatusNotDetermined
import platform.Foundation.timeIntervalSince1970
import platform.UIKit.UIDevice

internal val CLAuthorizationStatus.toPermissionState: PermissionState
    get() = when (this) {
        kCLAuthorizationStatusAuthorizedAlways,
        kCLAuthorizationStatusAuthorizedWhenInUse -> PermissionState.Granted
        kCLAuthorizationStatusNotDetermined -> PermissionState.NotDetermined
        kCLAuthorizationStatusDenied -> PermissionState.DeniedForever
        else -> error("Unknown location authorization status $this")
    }

internal val Priority.toIosPriority: Double
    get() = when (this) {
        Priority.HighAccuracy -> platform.CoreLocation.kCLLocationAccuracyBestForNavigation
        Priority.Balanced -> platform.CoreLocation.kCLLocationAccuracyNearestTenMeters
        Priority.LowPower -> platform.CoreLocation.kCLLocationAccuracyKilometer
        Priority.Passive -> platform.CoreLocation.kCLLocationAccuracyThreeKilometers
    }

@OptIn(ExperimentalForeignApi::class)
internal fun CLLocation.toModel(): Location {
    val coordinates = coordinate().useContents {
        Coordinates(
            latitude = latitude,
            longitude = longitude,
        )
    }

    val speed = Speed(
        mps = speed.toFloat(),
        accuracy = speedAccuracy.toFloat(),
    )

    val courseAccuracy =
        if (UIDevice.currentDevice.systemVersion < "13.4") null
        else courseAccuracy
    
    val azimuth = Azimuth(
        degrees = course.toFloat(),
        accuracy = courseAccuracy?.toFloat(),
    )

    val altitude = Altitude(
        meters = altitude,
        accuracy = verticalAccuracy.toFloat(),
    )

    return Location(
        coordinates = coordinates,
        accuracy = horizontalAccuracy,
        altitude = altitude,
        speed = speed,
        azimuth = azimuth,
        timestampMillis = timestamp.timeIntervalSince1970.toLong(),
    )
}
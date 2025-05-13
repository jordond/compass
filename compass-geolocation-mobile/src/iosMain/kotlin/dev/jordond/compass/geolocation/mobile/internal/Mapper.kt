package dev.jordond.compass.geolocation.mobile.internal

import dev.jordond.compass.Altitude
import dev.jordond.compass.Azimuth
import dev.jordond.compass.Coordinates
import dev.jordond.compass.Location
import dev.jordond.compass.Priority
import dev.jordond.compass.Speed
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreLocation.CLLocation
import platform.Foundation.timeIntervalSince1970
import platform.UIKit.UIDevice

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
        timestampMillis = timestamp.timeIntervalSince1970.toLong() * 1000L,
    )
}

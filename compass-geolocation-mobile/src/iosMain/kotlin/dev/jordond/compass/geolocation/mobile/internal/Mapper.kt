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

    val speed = speed.toFloat()
        .takeIf { speedValue -> speedValue >= 0.0 && speedAccuracy >= 0.0 }
        ?.let { speedValue ->
            Speed(
                mps = speedValue,
                accuracy = speedAccuracy.toFloat(),
            )
        }

    val courseAccuracy = courseAccuracy.takeIf { UIDevice.currentDevice.systemVersion >= "13.4" }

    val azimuth = course.toFloat()
        .takeIf { azimuthValue -> azimuthValue >= 0.0 && (courseAccuracy == null || courseAccuracy >= 0.0) }
        ?.let { azimuthValue ->
            Azimuth(
                degrees = azimuthValue,
                accuracy = courseAccuracy?.toFloat(),
            )
        }

    val altitude = verticalAccuracy.toFloat()
        .takeIf { verticalAccuracyValue -> verticalAccuracyValue > 0.0 }
        ?.let { verticalAccuracyValue ->
            Altitude(
                meters = altitude,
                accuracy = verticalAccuracyValue,
            )
        }

    return Location(
        coordinates = coordinates,
        accuracy = horizontalAccuracy,
        altitude = altitude,
        speed = speed,
        azimuth = azimuth,
        timestampMillis = timestamp.timeIntervalSince1970.toLong() * 1000L,
    )
}

package dev.jordond.compass.geolocation.mobile.internal

import android.content.Context
import android.location.Location
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.core.location.LocationCompat
import androidx.core.location.altitude.AltitudeConverterCompat.addMslAltitudeToLocation
import dev.jordond.compass.Altitude
import dev.jordond.compass.Azimuth
import dev.jordond.compass.Coordinates
import dev.jordond.compass.Speed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Converts a [Location] to a [dev.jordond.compass.Location].
 */
internal suspend fun Location.toModel(context: Context): dev.jordond.compass.Location {
    // Defensive copy to prevent ConcurrentModificationException when multiple
    // collectors of the SharedFlow invoke toModel on the same Location instance.
    val location = Location(this)
    return withContext(Dispatchers.IO) {
        if (location.hasAltitude()) {
            try {
                addMslAltitudeToLocation(context, location)
            } catch (_: Exception) {
                // MSL altitude conversion failed; continue without it
            }
        }

        dev.jordond.compass.Location(
            coordinates =
                Coordinates(
                    latitude = location.latitude,
                    longitude = location.longitude,
                ),
            accuracy = location.accuracy.toDouble(),
            azimuth =
                if (!hasBearing()) {
                    null
                } else {
                    Azimuth(
                        degrees = location.bearing,
                        accuracy =
                            if (VERSION.SDK_INT < VERSION_CODES.O || !location.hasBearingAccuracy()) {
                                null
                            } else {
                                location.bearingAccuracyDegrees
                            }
                    )
                },
            speed =
                if (!hasSpeed()) {
                    null
                } else {
                    Speed(
                        mps = location.speed,
                        accuracy =
                            if (VERSION.SDK_INT < VERSION_CODES.O || !location.hasSpeedAccuracy()) {
                                null
                            } else {
                                location.speedAccuracyMetersPerSecond
                            },
                    )
                },
            mslAltitude =
                if (!LocationCompat.hasMslAltitude(location)) {
                    null
                } else {
                    Altitude(
                        meters = LocationCompat.getMslAltitudeMeters(location),
                        accuracy =
                            if (!LocationCompat.hasMslAltitudeAccuracy(location)) {
                                null
                            } else {
                                LocationCompat.getMslAltitudeAccuracyMeters(location)
                            }
                    )
                },
            ellipsoidalAltitude =
                if (!location.hasAltitude()) {
                    null
                } else {
                    Altitude(
                        meters = location.altitude,
                        accuracy =
                            if (VERSION.SDK_INT < VERSION_CODES.O || !location.hasVerticalAccuracy()) {
                                null
                            } else {
                                location.verticalAccuracyMeters
                            },
                    )
                },
            timestampMillis = location.time,
        )
    }
}

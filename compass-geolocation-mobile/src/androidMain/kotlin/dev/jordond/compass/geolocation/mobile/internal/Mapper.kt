package dev.jordond.compass.geolocation.mobile.internal

import android.content.Context
import android.location.Location
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.core.location.LocationCompat
import androidx.core.location.altitude.AltitudeConverterCompat.addMslAltitudeToLocation
import com.google.android.gms.location.LocationRequest
import dev.jordond.compass.Altitude
import dev.jordond.compass.Azimuth
import dev.jordond.compass.Coordinates
import dev.jordond.compass.Priority
import dev.jordond.compass.Speed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import dev.jordond.compass.geolocation.LocationRequest as CompassLocationRequest

/**
 * Converts a [Location] to a [dev.jordond.compass.Location].
 */
internal suspend fun Location.toModel(context: Context): dev.jordond.compass.Location {
    // Defensive copy to prevent ConcurrentModificationException when multiple
    // collectors of the SharedFlow invoke toModel on the same Location instance.
    val location = Location(this)
    return withContext(Dispatchers.IO) {
        addMslAltitudeToLocation(context, location)

        dev.jordond.compass.Location(
            coordinates =
                Coordinates(
                    latitude = location.latitude,
                    longitude = location.longitude,
                ),
            accuracy = location.accuracy.toDouble(),
            azimuth =
                Azimuth(
                    degrees = location.bearing,
                    accuracy =
                        if (VERSION.SDK_INT < VERSION_CODES.O) {
                            null
                        } else {
                            location.bearingAccuracyDegrees
                        },
                ),
            speed =
                Speed(
                    mps = location.speed,
                    accuracy =
                        if (VERSION.SDK_INT < VERSION_CODES.O) {
                            null
                        } else {
                            location.speedAccuracyMetersPerSecond
                        },
                ),
            mslAltitude =
                Altitude(
                    meters =
                        if (!LocationCompat.hasMslAltitude(location)) {
                            null
                        } else {
                            LocationCompat.getMslAltitudeMeters(location)
                        },
                    accuracy =
                        if (!LocationCompat.hasMslAltitudeAccuracy(location)) {
                            null
                        } else {
                            LocationCompat.getMslAltitudeAccuracyMeters(location)
                        },
                ),
            ellipsoidalAltitude =
                Altitude(
                    meters = if (!location.hasAltitude()) null else location.altitude,
                    accuracy =
                        if (VERSION.SDK_INT < VERSION_CODES.O || !location.hasVerticalAccuracy()) {
                            null
                        } else {
                            location.verticalAccuracyMeters
                        },
                ),
            timestampMillis = location.time,
        )
    }
}

internal val Priority.toAndroidPriority: Int
    get() =
        when (this) {
            Priority.Balanced -> com.google.android.gms.location.Priority.PRIORITY_BALANCED_POWER_ACCURACY
            Priority.HighAccuracy -> com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
            Priority.LowPower -> com.google.android.gms.location.Priority.PRIORITY_LOW_POWER
            Priority.Passive -> com.google.android.gms.location.Priority.PRIORITY_PASSIVE
        }

internal fun CompassLocationRequest.toAndroidLocationRequest(): LocationRequest =
    LocationRequest
        .Builder(priority.toAndroidPriority, interval)
        .setGranularity(com.google.android.gms.location.Granularity.GRANULARITY_PERMISSION_LEVEL)
        .build()

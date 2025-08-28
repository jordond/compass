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
    return withContext(Dispatchers.IO) {
        addMslAltitudeToLocation(context, this@toModel)

        dev.jordond.compass.Location(
            coordinates = Coordinates(latitude = latitude, longitude = longitude),
            accuracy = accuracy.toDouble(),
            azimuth = Azimuth(
                degrees = bearing,
                accuracy = if (VERSION.SDK_INT < VERSION_CODES.O) null else bearingAccuracyDegrees,
            ),
            speed = Speed(
                mps = speed,
                accuracy = if (VERSION.SDK_INT < VERSION_CODES.O)
                    null else speedAccuracyMetersPerSecond,
            ),
            mslAltitude = Altitude(
                meters = if (!LocationCompat.hasMslAltitude(this@toModel))
                    null else LocationCompat.getMslAltitudeMeters(this@toModel),
                accuracy = if (!LocationCompat.hasMslAltitudeAccuracy(this@toModel))
                    null else LocationCompat.getMslAltitudeAccuracyMeters(this@toModel),
            ),
            ellipsoidalAltitude = Altitude(
                meters = if (!hasAltitude()) null else altitude,
                accuracy = if (VERSION.SDK_INT < VERSION_CODES.O || !hasVerticalAccuracy())
                    null else verticalAccuracyMeters,
            ),
            timestampMillis = time,
        )
    }
}

internal val Priority.toAndroidPriority: Int
    get() = when (this) {
        Priority.Balanced -> com.google.android.gms.location.Priority.PRIORITY_BALANCED_POWER_ACCURACY
        Priority.HighAccuracy -> com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
        Priority.LowPower -> com.google.android.gms.location.Priority.PRIORITY_LOW_POWER
        Priority.Passive -> com.google.android.gms.location.Priority.PRIORITY_PASSIVE
    }

internal fun CompassLocationRequest.toAndroidLocationRequest(): LocationRequest {
    return LocationRequest
        .Builder(priority.toAndroidPriority, interval)
        .setGranularity(com.google.android.gms.location.Granularity.GRANULARITY_PERMISSION_LEVEL)
        .build()
}

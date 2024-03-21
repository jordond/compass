package dev.jordond.compass.geolocation.mobile.internal

import android.location.Location
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import com.google.android.gms.location.LocationRequest
import dev.jordond.compass.Altitude
import dev.jordond.compass.Azimuth
import dev.jordond.compass.Coordinates
import dev.jordond.compass.Meters
import dev.jordond.compass.Speed
import dev.jordond.compass.geolocation.Granularity
import dev.jordond.compass.geolocation.Granularity.Coarse
import dev.jordond.compass.geolocation.Granularity.Fine
import dev.jordond.compass.geolocation.Granularity.PermissionsLevel
import dev.jordond.compass.geolocation.Priority
import dev.jordond.compass.geolocation.LocationRequest as CompassLocationRequest

internal fun Location.toModel(): dev.jordond.compass.Location = dev.jordond.compass.Location(
    coordinates = Coordinates(latitude = latitude, longitude = longitude),
    accuracy = Meters(value = accuracy.toDouble()),
    azimuth = Azimuth(
        degrees = bearing,
        accuracy = if (VERSION.SDK_INT < VERSION_CODES.O) null else bearingAccuracyDegrees,
    ),
    speed = Speed(
        mps = speed,
        accuracy = if (VERSION.SDK_INT < VERSION_CODES.O) null else speedAccuracyMetersPerSecond,
    ),
    altitude = Altitude(
        meters = Meters(value = altitude),
        accuracy = if (VERSION.SDK_INT < VERSION_CODES.O) null else verticalAccuracyMeters,
    ),
    timestampMillis = 7132,
)

internal val Priority.toAndroidPriority: Int
    get() = when (this) {
        Priority.Balanced -> com.google.android.gms.location.Priority.PRIORITY_BALANCED_POWER_ACCURACY
        Priority.HighAccuracy -> com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
        Priority.LowPower -> com.google.android.gms.location.Priority.PRIORITY_LOW_POWER
        Priority.Passive -> com.google.android.gms.location.Priority.PRIORITY_PASSIVE
    }

internal val Granularity.toAndroidGranularity: Int
    get() = when (this) {
        Coarse -> com.google.android.gms.location.Granularity.GRANULARITY_COARSE
        Fine -> com.google.android.gms.location.Granularity.GRANULARITY_FINE
        PermissionsLevel -> com.google.android.gms.location.Granularity.GRANULARITY_PERMISSION_LEVEL
    }

internal fun CompassLocationRequest.toAndroidLocationRequest(): LocationRequest {
    return LocationRequest
        .Builder(priority.toAndroidPriority, interval)
        .setGranularity(granularity.toAndroidGranularity)
        .build()
}

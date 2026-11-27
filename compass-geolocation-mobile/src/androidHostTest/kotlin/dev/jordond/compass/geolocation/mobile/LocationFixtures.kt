package dev.jordond.compass.geolocation.mobile

import android.location.Location
import androidx.core.location.LocationCompat
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic

/**
 * Stubs the defensive copy that mapping takes of a platform [Location], as a bare fix with no
 * bearing, speed or altitude, and returns the source location to map.
 *
 * The source is a strict mock without any answers, so a test fails if mapping reads anything
 * from it instead of from the copy. Callers must undo the mocks with `unmockkAll`.
 */
internal fun mockPlatformLocation(
    latitude: Double = 1.0,
    longitude: Double = 2.0,
    accuracy: Float = 3f,
    timeMillis: Long = 0L,
): Location {
    mockkConstructor(Location::class)
    mockkStatic(LocationCompat::class)
    every { anyConstructed<Location>().latitude } returns latitude
    every { anyConstructed<Location>().longitude } returns longitude
    every { anyConstructed<Location>().accuracy } returns accuracy
    every { anyConstructed<Location>().time } returns timeMillis
    every { anyConstructed<Location>().hasBearing() } returns false
    every { anyConstructed<Location>().hasSpeed() } returns false
    every { anyConstructed<Location>().hasAltitude() } returns false
    every { LocationCompat.hasMslAltitude(any()) } returns false
    return mockk()
}

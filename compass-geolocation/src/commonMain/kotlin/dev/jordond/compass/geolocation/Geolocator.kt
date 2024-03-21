package dev.jordond.compass.geolocation

import dev.jordond.compass.ExtendedLocation
import dev.jordond.compass.Location
import dev.jordond.compass.geolocation.internal.DefaultGeolocator
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

public interface Geolocator {

    public fun isAvailable(): Boolean

    public suspend fun last(): GeolocatorResult<Location>

    public suspend fun current(): GeolocatorResult<Location>

    public suspend fun extended(): GeolocatorResult<ExtendedLocation>

    public fun track(): Flow<Location>

    public fun trackExtended(): Flow<ExtendedLocation>

    public fun stopTracking()
}

public fun Geolocator(
    locator: Locator,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Geolocator = DefaultGeolocator(locator, dispatcher)
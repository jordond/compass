package dev.jordond.compass.geolocation

import dev.jordond.compass.Location
import dev.jordond.compass.geolocation.internal.DefaultGeolocator
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

public interface Geolocator {

    public fun isAvailable(): Boolean

    public suspend fun last(): GeolocatorResult<Location?>

    public suspend fun current(priority: Priority = Priority.Balanced): GeolocatorResult<Location>

    public fun track(request: LocationRequest = LocationRequest()): Flow<Location>

    public fun stopTracking()
}

public fun Geolocator(
    locator: Locator,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Geolocator = DefaultGeolocator(locator, dispatcher)
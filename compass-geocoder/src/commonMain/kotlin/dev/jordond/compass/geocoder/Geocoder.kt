package dev.jordond.compass.geocoder

import dev.drewhamilton.poko.Poko
import dev.jordond.compass.Location
import dev.jordond.compass.Place
import dev.jordond.compass.geocoder.internal.DefaultGeocoder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

public interface Geocoder {

    public fun available(): Boolean

    public suspend fun reverseGeocode(coordinates: Location): Result

    public suspend fun reverseGeocode(latitude: Double, longitude: Double): Result =
        reverseGeocode(Location(latitude, longitude))

    public suspend fun reverseGeocodeOrNull(coordinates: Location): Place? =
        reverseGeocode(coordinates).locationOrNull()

    public suspend fun reverseGeocodeOrNull(latitude: Double, longitude: Double): Place? =
        reverseGeocode(latitude, longitude).locationOrNull()

    public sealed interface Result {

        public interface Error : Result

        @Poko
        public class Success(public val place: Place) : Result

        public object NotFound : Error

        @Poko
        public class GeocodeFailed(public val message: String) : Error
        public object NotSupported : Error
        public object InvalidCoordinates : Error

        public val isError: Boolean
            get() = this is Error

        public fun locationOrNull(): Place? = if (this is Success) place else null

        public fun errorOrNull(): Error? = if (this is Error) this else null
    }
}

public fun Geocoder(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Geocoder = DefaultGeocoder(dispatcher)

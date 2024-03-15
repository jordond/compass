package dev.jordond.compass.geocoder

import dev.drewhamilton.poko.Poko
import dev.jordond.compass.Place

public sealed interface GeocoderResult {

    public interface Error : GeocoderResult

    @Poko
    public class Success(public val places: List<Place>) : GeocoderResult

    public object NotFound : Error

    @Poko
    public class GeocodeFailed(public val message: String) : Error
    public object NotSupported : Error
    public object InvalidCoordinates : Error

    public val isError: Boolean
        get() = this is Error

    public fun locationsOrNull(): List<Place>? = if (this is Success) places else null

    public fun locationOrNull(): Place? = locationsOrNull()?.firstOrNull()

    public fun errorOrNull(): Error? = if (this is Error) this else null

    public fun onSuccess(block: (List<Place>) -> Unit): GeocoderResult {
        if (this is Success) block(places)
        return this
    }

    public fun onFailed(block: (Error) -> Unit): GeocoderResult {
        if (this is Error) block(this)
        return this
    }
}
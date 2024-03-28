package dev.jordond.compass

import dev.drewhamilton.poko.Poko

/**
 * Represents a place on Earth.
 *
 * The geocoder might not always return all of these properties, so you must check each one before
 * using it.
 *
 * @property coordinates The geographic coordinates of the place.
 * @property name The name of the place.
 * @property street The street of the place.
 * @property isoCountryCode The ISO country code of the place.
 * @property country The country of the place.
 * @property postalCode The postal code of the place.
 * @property administrativeArea The administrative area of the place.
 * @property subAdministrativeArea The sub-administrative area of the place.
 * @property locality The locality of the place.
 * @property subLocality The sub-locality of the place.
 * @property thoroughfare The thoroughfare of the place.
 * @property subThoroughfare The sub-thoroughfare of the place.
 *
 */
@Poko
public class Place(
    public val coordinates: Coordinates,
    public val name: String?,
    public val street: String?,
    public val isoCountryCode: String?,
    public val country: String?,
    public val postalCode: String?,
    public val administrativeArea: String?,
    public val subAdministrativeArea: String?,
    public val locality: String?,
    public val subLocality: String?,
    public val thoroughfare: String?,
    public val subThoroughfare: String?,
) {

    /**
     * Check if all of the properties are null.
     *
     * @return True if all properties are null, false otherwise.
     */
    public val isEmpty: Boolean = name == null && street == null && isoCountryCode == null &&
        country == null && postalCode == null && administrativeArea == null &&
        subAdministrativeArea == null && locality == null && subLocality == null &&
        thoroughfare == null && subThoroughfare == null

    /**
     * Get the first non-null value of the place.
     *
     * Ensure [isEmpty] returns `true` before calling this method, or catch the exception.
     *
     * @return The first non-null value of the place.
     * @throws IllegalStateException If the place is empty.
     */
    public val firstValue: String
        get() = name ?: street ?: thoroughfare ?: subThoroughfare ?: subLocality ?: locality
        ?: subAdministrativeArea ?: administrativeArea ?: postalCode ?: country ?: isoCountryCode
        ?: throw IllegalStateException("Place is empty")

    /**
     * Used for testing purposes.
     */
    internal fun nullCopy(
        coordinates: Coordinates = this.coordinates,
        name: String? = null,
        street: String? = null,
        isoCountryCode: String? = null,
        country: String? = null,
        postalCode: String? = null,
        administrativeArea: String? = null,
        subAdministrativeArea: String? = null,
        locality: String? = null,
        subLocality: String? = null,
        thoroughfare: String? = null,
        subThoroughfare: String? = null,
    ): Place = Place(
        coordinates = coordinates,
        name = name,
        street = street,
        isoCountryCode = isoCountryCode,
        country = country,
        postalCode = postalCode,
        administrativeArea = administrativeArea,
        subAdministrativeArea = subAdministrativeArea,
        locality = locality,
        subLocality = subLocality,
        thoroughfare = thoroughfare,
        subThoroughfare = subThoroughfare,
    )
}
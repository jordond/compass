package dev.jordond.compass

import dev.drewhamilton.poko.Poko

@Poko
public class Place(
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

    public val isEmpty: Boolean = name == null && street == null && isoCountryCode == null &&
        country == null && postalCode == null && administrativeArea == null &&
        subAdministrativeArea == null && locality == null && subLocality == null &&
        thoroughfare == null && subThoroughfare == null

    /**
     * Used for testing purposes.
     */
    internal fun nullCopy(
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
        subThoroughfare = subThoroughfare
    )
}
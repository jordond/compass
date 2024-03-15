package dev.jordond.compass.geocoder

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
)

internal val Place.isEmpty: Boolean
    get() = name == null && street == null && isoCountryCode == null && country == null &&
        postalCode == null && administrativeArea == null && subAdministrativeArea == null &&
        locality == null && subLocality == null && thoroughfare == null && subThoroughfare == null
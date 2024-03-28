package dev.jordond.compass.geocoder.web.mapbox.internal

import dev.jordond.compass.InternalCompassApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@InternalCompassApi
@Serializable
public data class ContextResponse(
    @SerialName("address")
    public val address: AddressDataPoint? = null,

    @SerialName("street")
    public val street: DataPoint? = null,

    @SerialName("neighbourhood")
    public val neighborhood: DataPoint? = null,

    @SerialName("postcode")
    public val postcode: DataPoint? = null,

    @SerialName("locality")
    public val locality: DataPoint? = null,

    @SerialName("place")
    public val place: DataPoint? = null,

    @SerialName("district")
    public val district: DataPoint? = null,

    @SerialName("region")
    public val region: RegionDataPoint? = null,

    @SerialName("country")
    public val country: CountryDataPoint? = null,
)

@InternalCompassApi
@Serializable
public data class DataPoint(
    @SerialName("name")
    public val name: String? = null,
)

@InternalCompassApi
@Serializable
public data class AddressDataPoint(
    @SerialName("name")
    public val name: String? = null,

    @SerialName("street_name")
    public val streetName: String? = null,

    @SerialName("address_number")
    public val addressNumber: String? = null,
)

@InternalCompassApi
@Serializable
public data class RegionDataPoint(
    @SerialName("name")
    public val name: String? = null,

    @SerialName("region_code")
    public val regionCode: String? = null,
)

@InternalCompassApi
@Serializable
public data class CountryDataPoint(
    @SerialName("name")
    public val name: String? = null,

    @SerialName("country_code")
    public val countryCode: String? = null,
)
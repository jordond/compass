package dev.jordond.compass.geocoder.web.internal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ContextResponse(
    @SerialName("address")
    val address: AddressDataPoint? = null,

    @SerialName("street")
    val street: DataPoint? = null,

    @SerialName("neighbourhood")
    val neighborhood: DataPoint? = null,

    @SerialName("postcode")
    val postcode: DataPoint? = null,

    @SerialName("locality")
    val locality: DataPoint? = null,

    @SerialName("place")
    val place: DataPoint? = null,

    @SerialName("district")
    val district: DataPoint? = null,

    @SerialName("region")
    val region: RegionDataPoint? = null,

    @SerialName("country")
    val country: CountryDataPoint? = null,
)

@Serializable
internal data class DataPoint(
    @SerialName("name")
    val name: String? = null,
)

@Serializable
internal data class AddressDataPoint(
    @SerialName("name")
    val name: String? = null,

    @SerialName("street_name")
    val streetName: String? = null,

    @SerialName("address_number")
    val addressNumber: String? = null,
)

@Serializable
internal data class RegionDataPoint(
    @SerialName("name")
    val name: String? = null,

    @SerialName("region_code")
    val regionCode: String? = null,
)

@Serializable
internal data class CountryDataPoint(
    @SerialName("name")
    val name: String? = null,

    @SerialName("country_code")
    val countryCode: String? = null,
)
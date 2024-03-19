package dev.jordond.compass.geocoder.web.internal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ContextResponse(
    @SerialName("address")
    val address: DataPoint,

    @SerialName("street")
    val street: DataPoint,

    @SerialName("neighbourhood")
    val neighborhood: DataPoint,

    @SerialName("postcode")
    val postcode: DataPoint,

    @SerialName("locality")
    val locality: DataPoint,

    @SerialName("place")
    val place: DataPoint,

    @SerialName("district")
    val district: DataPoint,

    @SerialName("region")
    val region: RegionDataPoint,

    @SerialName("country")
    val country: CountryDataPoint,
)

@Serializable
internal open class DataPoint(
    @SerialName("name")
    val name: String,
)

@Serializable
internal data class RegionDataPoint(
    @SerialName("region_code")
    val regionCode: String,
)

@Serializable
internal data class CountryDataPoint(
    @SerialName("country_code")
    val countryCode: String,
)
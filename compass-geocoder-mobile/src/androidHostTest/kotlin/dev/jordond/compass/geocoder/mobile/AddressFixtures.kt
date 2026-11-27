package dev.jordond.compass.geocoder.mobile

import android.location.Address
import io.mockk.every
import io.mockk.mockk

/**
 * A platform [Address] at the given coordinates, with every unspecified field reported as missing.
 */
internal fun address(
    latitude: Double,
    longitude: Double,
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
): Address {
    val address = mockk<Address>()
    every { address.latitude } returns latitude
    every { address.longitude } returns longitude
    every { address.featureName } returns name
    every { address.getAddressLine(0) } returns street
    every { address.countryCode } returns isoCountryCode
    every { address.countryName } returns country
    every { address.postalCode } returns postalCode
    every { address.adminArea } returns administrativeArea
    every { address.subAdminArea } returns subAdministrativeArea
    every { address.locality } returns locality
    every { address.subLocality } returns subLocality
    every { address.thoroughfare } returns thoroughfare
    every { address.subThoroughfare } returns subThoroughfare
    return address
}

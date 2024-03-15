package dev.jordond.compass.geocoder.internal

import dev.jordond.compass.Place
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Contacts.CNPostalAddress
import platform.CoreLocation.CLPlacemark
import platform.CoreLocation.postalAddress

@Suppress("CAST_NEVER_SUCCEEDS")
@OptIn(ExperimentalForeignApi::class)
internal fun CLPlacemark.toPlace(): Place {
    val street = runCatching { (postalAddress as? CNPostalAddress)?.street }.getOrNull()
    return Place(
        name = name,
        street = street,
        isoCountryCode = ISOcountryCode,
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
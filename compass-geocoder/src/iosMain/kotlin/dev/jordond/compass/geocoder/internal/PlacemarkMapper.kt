package dev.jordond.compass.geocoder.internal

import dev.jordond.compass.Location
import dev.jordond.compass.Place
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.Contacts.CNPostalAddress
import platform.CoreLocation.CLPlacemark
import platform.CoreLocation.postalAddress

/**
 * Map a [CLPlacemark] to a [Place].
 */
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

internal fun List<CLPlacemark>.toPlaces(): List<Place> = map { it.toPlace() }

@OptIn(ExperimentalForeignApi::class)
internal fun CLPlacemark.toLocation(): Location? {
    val (latitude, longitude) = location?.coordinate?.useContents { latitude to longitude }
        ?: return null

    return Location(
        latitude = latitude,
        longitude = longitude,
    )
}

internal fun List<CLPlacemark>.toLocations(): List<Location> = mapNotNull { it.toLocation() }
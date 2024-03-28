package dev.jordond.compass.autocomplete.mobile.internal

import dev.jordond.compass.Place
import dev.jordond.compass.autocomplete.AutocompleteService
import dev.jordond.compass.geocoder.mobile.MobilePlatformGeocoder

internal class DefaultMobileAutocompleteService(
    private val platformGeocoder: MobilePlatformGeocoder,
) : AutocompleteService<Place> {

    override fun isAvailable(): Boolean = platformGeocoder.isAvailable()

    override suspend fun search(query: String): List<Place> {
        return platformGeocoder.placeFromAddress(query)
    }
}
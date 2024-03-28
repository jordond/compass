package dev.jordond.compass.autocomplete.mobile

import dev.jordond.compass.Place
import dev.jordond.compass.autocomplete.AutocompleteService
import dev.jordond.compass.autocomplete.mobile.internal.DefaultMobileAutocompleteService
import dev.jordond.compass.geocoder.mobile.MobilePlatformGeocoder

@Suppress("FunctionName")
public fun MobileAutocompleteService(
    platformGeocoder: MobilePlatformGeocoder = MobilePlatformGeocoder(),
): AutocompleteService<Place> = DefaultMobileAutocompleteService(
    platformGeocoder = platformGeocoder,
)

public fun AutocompleteService.Companion.mobile(): AutocompleteService<Place> =
    MobileAutocompleteService()
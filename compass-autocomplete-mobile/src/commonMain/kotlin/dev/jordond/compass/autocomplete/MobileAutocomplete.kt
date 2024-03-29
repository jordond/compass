package dev.jordond.compass.autocomplete

import dev.jordond.compass.Place
import dev.jordond.compass.autocomplete.mobile.mobile
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

public fun Autocomplete(
    options: AutocompleteOptions = AutocompleteOptions(),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Autocomplete<Place> = MobileAutocomplete(options, dispatcher)

@Suppress("FunctionName")
public fun MobileAutocomplete(
    options: AutocompleteOptions = AutocompleteOptions(),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Autocomplete<Place> = Autocomplete.mobile(options, dispatcher)

public fun Autocomplete.Companion.mobile(
    options: AutocompleteOptions = AutocompleteOptions(),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Autocomplete<Place> = Autocomplete(
    service = AutocompleteService.mobile(),
    options = options,
    dispatcher = dispatcher
)
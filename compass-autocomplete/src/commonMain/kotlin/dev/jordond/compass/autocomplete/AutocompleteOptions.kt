package dev.jordond.compass.autocomplete

import dev.drewhamilton.poko.Poko

@Poko
public open class AutocompleteOptions(
    public val minimumQuery: Int = DEFAULT_MINIMUM_QUERY,
) {

    internal companion object {

        private const val DEFAULT_MINIMUM_QUERY = 3
    }
}
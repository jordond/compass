package dev.jordond.compass.autocomplete

import dev.drewhamilton.poko.Poko

/**
 * Options for configuring an [Autocomplete].
 *
 * @property minimumQuery The minimum number of characters required in a query before an
 * autocomplete request is made.
 */
@Poko
public open class AutocompleteOptions(
    public val minimumQuery: Int = DEFAULT_MINIMUM_QUERY,
) {

    internal companion object {

        private const val DEFAULT_MINIMUM_QUERY = 3
    }
}
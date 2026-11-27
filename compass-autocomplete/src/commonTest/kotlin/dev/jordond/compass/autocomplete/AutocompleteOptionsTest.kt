package dev.jordond.compass.autocomplete

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class AutocompleteOptionsTest {

    @Test
    fun defaultMinimumQueryIsThree() {
        assertEquals(3, AutocompleteOptions().minimumQuery)
    }

    @Test
    fun customMinimumQueryIsKept() {
        assertEquals(7, AutocompleteOptions(minimumQuery = 7).minimumQuery)
    }

    @Test
    fun optionsHaveValueEquality() {
        assertEquals(AutocompleteOptions(2), AutocompleteOptions(2))
        assertNotEquals(AutocompleteOptions(2), AutocompleteOptions(4))
    }
}

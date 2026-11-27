package dev.jordond.compass.exception

import kotlin.test.Test
import kotlin.test.assertEquals

class ExceptionsTest {

    @Test
    fun notSupportedExceptionHasFixedMessage() {
        assertEquals("Operation is not supported on this device", NotSupportedException().message)
    }

    @Test
    fun notFoundExceptionHasFixedMessage() {
        assertEquals("Unable to find requested result.", NotFoundException().message)
    }
}

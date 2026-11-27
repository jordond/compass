package dev.jordond.compass.tools.web.exception

import io.ktor.http.HttpStatusCode
import kotlin.test.Test
import kotlin.test.assertEquals

class WebExceptionTest {

    @Test
    fun exposesStatusCodeAndMessage() {
        val exception = WebException(HttpStatusCode.NotFound, "missing")
        assertEquals(HttpStatusCode.NotFound, exception.statusCode)
        assertEquals("missing", exception.message)
    }
}

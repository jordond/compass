package dev.jordond.compass.tools.web.exception

import io.ktor.http.HttpStatusCode

/**
 * Represents an exception that occurred during a web request.
 *
 * @property statusCode The status code of the response.
 * @property message The message of the exception.
 */
public class WebException(
    public val statusCode: HttpStatusCode,
    public override val message: String,
) : Throwable(message)
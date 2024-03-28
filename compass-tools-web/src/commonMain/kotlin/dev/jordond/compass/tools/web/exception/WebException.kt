package dev.jordond.compass.tools.web.exception

import io.ktor.http.HttpStatusCode

public class WebException(
    public val statusCode: HttpStatusCode,
    public override val message: String,
) : Throwable(message)
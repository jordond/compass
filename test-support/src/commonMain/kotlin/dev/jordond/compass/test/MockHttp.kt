package dev.jordond.compass.test

import dev.jordond.compass.InternalCompassApi
import dev.jordond.compass.tools.web.HttpApiEndpoint
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.http.Url
import io.ktor.http.headersOf

/**
 * A recording HTTP stand-in for tests.
 *
 * [client] is the client the library builds by default, see [HttpApiEndpoint.httpClient], running
 * on a mock engine, so a test exercises the same error handling and JSON setup a consumer gets.
 * Retries are off unless [enableRetry] is set, since they would only repeat the canned response.
 *
 * @param enableRetry Whether the client retries server errors like the default client does.
 * @param handler Produces the response for each request.
 */
@OptIn(InternalCompassApi::class)
public class MockHttp(
    enableRetry: Boolean = false,
    private val handler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData,
) {

    private val recorded = mutableListOf<HttpRequestData>()

    /**
     * Every request the [client] has made, oldest first.
     */
    public val requests: List<HttpRequestData> get() = recorded

    /**
     * The URL of the only request made. Fails when there were none or several.
     */
    public val url: Url get() = recorded.single().url

    /**
     * The decoded query parameters of the only request made.
     */
    public val parameters: Parameters get() = url.parameters

    /**
     * Every decoded query parameter of the only request made, so a test can assert the whole set.
     */
    public val query: Map<String, String>
        get() = parameters.entries().associate { (name, values) -> name to values.single() }

    /**
     * The URL of the only request made, without its query.
     */
    public val urlWithoutQuery: String get() = url.toString().substringBefore(delimiter = "?")

    public val client: HttpClient = HttpApiEndpoint.httpClient(
        engine = MockEngine { request ->
            recorded += request
            handler(request)
        },
        enableRetry = enableRetry,
    )
}

/**
 * A [MockHttp] that answers every request with the same JSON [body] and [status].
 */
public fun MockHttp(
    body: String,
    status: HttpStatusCode = HttpStatusCode.OK,
    enableRetry: Boolean = false,
): MockHttp = MockHttp(enableRetry = enableRetry) { jsonResponse(body, status) }

/**
 * Respond with a JSON [body].
 */
public fun MockRequestHandleScope.jsonResponse(
    body: String,
    status: HttpStatusCode = HttpStatusCode.OK,
): HttpResponseData = respond(
    content = body,
    status = status,
    headers = headersOf(HttpHeaders.ContentType, "application/json"),
)

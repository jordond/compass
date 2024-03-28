package dev.jordond.compass.tools.web

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.statement.HttpResponse
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder

/**
 * Defines an HTTP API endpoint.
 *
 * @param Params The type of the parameters to be passed to the endpoint.
 * @param Result The type of the result returned after mapping the response.
 */
public interface HttpApiEndpoint<Params, Result> {

    /**
     * Returns the URL for the endpoint with the given parameters.
     *
     * @param param The parameters to be passed to the endpoint.
     * @return The URL for the endpoint with the given parameters.
     */
    public fun url(param: Params): String

    /**
     * Maps the [HttpResponse] to the [Result] type.
     *
     * @param response The response to be mapped.
     * @return The result type.
     */
    public suspend fun mapResponse(response: HttpResponse): Result

    public companion object {

        /**
         * Create a new [Json] configuration with the given [block].
         *
         * @param block Customization of the [Json] configuration.
         * @return A new [Json] configuration with the given [block].
         */
        public fun json(block: JsonBuilder.() -> Unit = {}): Json = Json {
            ignoreUnknownKeys = true
            apply(block)
        }

        /**
         * Create a new [HttpClient] with the given [json] configuration.
         *
         * Customization of the [HttpClient] can be done with the [block] parameter.
         *
         * @param json The [Json] configuration to use for the [HttpClient].
         * @param enableLogging Whether or not to enable logging.
         * @param enableRetry Whether or not to enable retrying 500 errors.
         * @param maxRetries The maximum number of retries to attempt.
         * @param block Customization of the [HttpClient].
         * @return A new [HttpClient] with the given [json] configuration.
         */
        public fun httpClient(
            json: Json = json(),
            enableLogging: Boolean = false,
            enableRetry: Boolean = true,
            maxRetries: Int = 3,
            block: HttpClientConfig<*>.() -> Unit = {},
        ): HttpClient = HttpClient {
            expectSuccess = true

            if (enableRetry) {
                install(HttpRequestRetry) {
                    retryOnServerErrors(maxRetries = maxRetries)
                    exponentialDelay()
                }
            }

            if (enableLogging) {
                install(Logging) {
                    level = LogLevel.ALL
                    logger = object : Logger {
                        override fun log(message: String) {
                            co.touchlab.kermit.Logger.i { message }
                        }
                    }
                }
            }

            install(ContentNegotiation) {
                json(json)
            }

            apply(block)
        }
    }
}

/**
 * Creates an [HttpApiEndpoint] with the given URL and response mapping.
 *
 * @param Params The type of the parameters to be passed to the endpoint.
 * @param Result The type of the result returned after mapping the response.
 * @param url The URL for the endpoint.
 * @param mapResponse The response mapping.
 * @return The created [HttpApiEndpoint].
 */
public fun <Params, Result> HttpApiEndpoint(
    url: (Params) -> String,
    mapResponse: suspend (HttpResponse) -> Result,
): HttpApiEndpoint<Params, Result> = object : HttpApiEndpoint<Params, Result> {
    override fun url(param: Params): String = url(param)
    override suspend fun mapResponse(response: HttpResponse): Result = mapResponse(response)
}
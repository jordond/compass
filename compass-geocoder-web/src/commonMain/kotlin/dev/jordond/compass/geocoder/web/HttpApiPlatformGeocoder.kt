package dev.jordond.compass.geocoder.web

import dev.jordond.compass.Coordinates
import dev.jordond.compass.Place
import dev.jordond.compass.geocoder.PlatformGeocoder
import dev.jordond.compass.tools.web.HttpApiEndpoint
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder

/**
 * Alias that denotes a forward geocoding [HttpApiEndpoint].
 */
public typealias ForwardEndpoint = HttpApiEndpoint<String, List<Coordinates>>

/**
 * Alias that denotes a reverse geocoding [HttpApiEndpoint].
 */
public typealias ReverseEndpoint = HttpApiEndpoint<Coordinates, List<Place>>

/**
 * Defines the interface for a geocoder that uses an HTTP API to perform geocoding operations.
 */
public interface HttpApiPlatformGeocoder : PlatformGeocoder {

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
 * Create a new [HttpApiPlatformGeocoder] with the given [forwardEndpoint] and [reverseEndpoint].
 *
 * Customization of the [Json] configuration and [HttpClient] can be done with
 * the [json] and [client] parameters.
 *
 * @param forwardEndpoint The endpoint to use for forward geocoding.
 * @param reverseEndpoint The endpoint to use for reverse geocoding.
 * @param json The [Json] configuration to use for the [HttpClient].
 * @param client The [HttpClient] to use for the geocoder.
 * @return A new [HttpApiPlatformGeocoder] with the given [forwardEndpoint] and [reverseEndpoint].
 */
public fun HttpApiPlatformGeocoder(
    forwardEndpoint: ForwardEndpoint,
    reverseEndpoint: ReverseEndpoint,
    json: Json = HttpApiPlatformGeocoder.json(),
    client: HttpClient = HttpApiPlatformGeocoder.httpClient(json),
): HttpApiPlatformGeocoder = object : HttpApiPlatformGeocoder {

    override fun isAvailable(): Boolean = true

    override suspend fun forward(address: String): List<Coordinates> {
        return ForwardHttpApiPlatformGeocoder(forwardEndpoint, client)
            .forward(address)
    }

    override suspend fun reverse(latitude: Double, longitude: Double): List<Place> {
        return ReverseHttpApiPlatformGeocoder(reverseEndpoint, client)
            .reverse(latitude, longitude)
    }
}
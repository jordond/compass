package dev.jordond.compass.geocoder.web

import dev.jordond.compass.geocoder.PlatformGeocoder
import dev.jordond.compass.geocoder.exception.GeocodeException
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder

public interface HttpApiPlatformGeocoder : PlatformGeocoder {

    @Suppress("MemberVisibilityCanBePrivate")
    public companion object {

        public fun json(block: JsonBuilder.() -> Unit = {}): Json = Json {
            ignoreUnknownKeys = true
            apply(block)
        }

        public fun httpClient(
            json: Json = json(),
            enableRetry: Boolean = true,
            maxRetries: Int = 5,
            block: HttpClientConfig<*>.() -> Unit = {},
        ): HttpClient = HttpClient {
            if (enableRetry) {
                install(HttpRequestRetry) {
                    retryOnServerErrors(maxRetries = maxRetries)
                    exponentialDelay()
                }
            }

            install(ContentNegotiation) {
                json(json)
            }

            apply(block)
        }
    }
}
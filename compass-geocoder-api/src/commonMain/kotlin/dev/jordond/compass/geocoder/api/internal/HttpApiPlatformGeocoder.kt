package dev.jordond.compass.geocoder.api.internal

import dev.jordond.compass.geocoder.PlatformGeocoder
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
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
            block: HttpClientConfig<*>.() -> Unit = {},
        ): HttpClient = HttpClient {
            install(ContentNegotiation) {
                json(json)
            }

            apply(block)
        }
    }
}

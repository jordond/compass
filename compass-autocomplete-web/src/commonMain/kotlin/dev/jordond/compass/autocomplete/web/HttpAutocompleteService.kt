package dev.jordond.compass.autocomplete.web

import dev.jordond.compass.autocomplete.AutocompleteService
import dev.jordond.compass.tools.web.HttpApiEndpoint
import dev.jordond.compass.tools.web.makeRequest
import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json

/**
 * Represents an HTTP endpoint that can be used to search for autocomplete suggestions.
 *
 * @param T The type of the autocomplete suggestions.
 */
public typealias SearchEndpoint <T> = HttpApiEndpoint<String, List<T>>

/**
 * Represents an autocomplete service that uses an HTTP endpoint to search for suggestions.
 *
 * @param T The type of the autocomplete suggestions.
 */
public interface HttpAutocompleteService<T> : AutocompleteService<T> {

    public companion object
}

/**
 * Creates an [HttpAutocompleteService] that uses the specified [searchEndpoint] to search for
 * autocomplete suggestions.
 *
 * @param T The type of the autocomplete suggestions.
 * @param searchEndpoint The HTTP endpoint to use for searching.
 * @param json The JSON serializer to use.
 * @param client The HTTP client to use.
 * @return An [HttpAutocompleteService] that uses the specified [searchEndpoint] to search for
 * autocomplete suggestions.
 */
public fun <T> HttpAutocompleteService(
    searchEndpoint: SearchEndpoint<T>,
    json: Json = HttpApiEndpoint.json(),
    client: HttpClient = HttpApiEndpoint.httpClient(json),
): HttpAutocompleteService<T> = object : HttpAutocompleteService<T> {

    override fun isAvailable(): Boolean = true

    override suspend fun search(query: String): List<T> {
        val url = searchEndpoint.url(query)
        return client.makeRequest(url, searchEndpoint::mapResponse)
    }
}
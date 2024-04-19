package dev.jordond.compass.autocomplete.web

import dev.jordond.compass.Place
import dev.jordond.compass.autocomplete.AutocompleteService
import dev.jordond.compass.autocomplete.QueryResult
import dev.jordond.compass.tools.web.HttpApiEndpoint
import dev.jordond.compass.tools.web.makeRequest
import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json

/**
 * Represents an HTTP endpoint that can be used to search for autocomplete suggestions.
 */
public typealias SearchEndpoint = HttpApiEndpoint<String, List<QueryResult>>

public typealias DetailsEndpoint = HttpApiEndpoint<String, Place>

/**
 * Represents an autocomplete service that uses an HTTP endpoint to search for suggestions.
 */
public interface HttpAutocompleteService : AutocompleteService<QueryResult> {

    public suspend fun details(id: String): Place

    public companion object
}

/**
 * Creates an [HttpAutocompleteService] that uses the specified [searchEndpoint] to search for
 * autocomplete suggestions.
 *
 * @param searchEndpoint The HTTP endpoint to use for searching.
 * @param json The JSON serializer to use.
 * @param client The HTTP client to use.
 * @return An [HttpAutocompleteService] that uses the specified [searchEndpoint] to search for
 * autocomplete suggestions.
 */
public fun HttpAutocompleteService(
    searchEndpoint: SearchEndpoint,
    detailsEndpoint: DetailsEndpoint,
    json: Json = HttpApiEndpoint.json(),
    client: HttpClient = HttpApiEndpoint.httpClient(json),
): HttpAutocompleteService = object : HttpAutocompleteService {

    override fun isAvailable(): Boolean = true

    override suspend fun search(query: String): List<QueryResult> {
        val url = searchEndpoint.url(query)
        return client.makeRequest(url, searchEndpoint::mapResponse)
    }

    override suspend fun details(id: String): Place {
        val url = detailsEndpoint.url(id)
        return client.makeRequest(url, detailsEndpoint::mapResponse)
    }
}
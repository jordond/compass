package dev.jordond.compass.autocomplete.web

import dev.jordond.compass.autocomplete.AutocompleteService
import dev.jordond.compass.tools.web.HttpApiEndpoint
import dev.jordond.compass.tools.web.makeRequest
import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json

public typealias SearchEndpoint <T> = HttpApiEndpoint<String, List<T>>

public interface HttpAutocompleteService<T> : AutocompleteService<T> {

    public companion object
}

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
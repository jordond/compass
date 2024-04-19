package dev.jordond.compass.autocomplete.web

import dev.jordond.compass.autocomplete.QueryResult
import dev.jordond.compass.autocomplete.web.google.internal.AutocompleteResponse
import dev.jordond.compass.autocomplete.web.google.internal.StatusResponse
import dev.jordond.compass.autocomplete.web.google.internal.toQueryResults
import dev.jordond.compass.tools.web.HttpApiEndpoint
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.encodeURLQueryComponent

public class GooglePlacesDetailsEndpoint(
    private val apiKey: String,
    private val sessionToken: String,
) : HttpApiEndpoint<String, List<QueryResult>> {

    override fun url(param: String): String {
        val encodedQuery = param.encodeURLQueryComponent()
        return GooglePlacesAutocompleteService
            .details(encodedQuery, apiKey, sessionToken)
    }

    override suspend fun mapResponse(response: HttpResponse): List<QueryResult> {
        val result = response.body<AutocompleteResponse>()
        if (result.status != StatusResponse.Ok) {
            throw IllegalStateException(
                "Google Places Autocomplete API returned status ${result.status}"
            )
        }

        return result.toQueryResults()
    }
}
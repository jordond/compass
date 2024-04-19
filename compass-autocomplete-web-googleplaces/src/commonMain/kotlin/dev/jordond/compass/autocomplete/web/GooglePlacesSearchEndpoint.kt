package dev.jordond.compass.autocomplete.web

import com.benasher44.uuid.uuid4
import dev.jordond.compass.autocomplete.QueryResult
import dev.jordond.compass.autocomplete.web.google.internal.AutocompleteResponse
import dev.jordond.compass.autocomplete.web.google.internal.StatusResponse
import dev.jordond.compass.autocomplete.web.google.internal.toQueryResults
import dev.jordond.compass.autocomplete.web.parameter.GooglePlacesParameters
import dev.jordond.compass.autocomplete.web.parameter.GooglePlacesParametersBuilder
import dev.jordond.compass.autocomplete.web.parameter.googlePlacesParameters
import dev.jordond.compass.tools.web.HttpApiEndpoint
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.encodeURLQueryComponent

public class GooglePlacesSearchEndpoint(
    private val apiKey: String,
    private val parameters: GooglePlacesParameters = GooglePlacesParameters(),
) : HttpApiEndpoint<String, List<QueryResult>> {

    internal var sessionToken: String = uuid4().toString()

    /**
     * Creates a new [GooglePlacesSearchEndpoint] with the given API key.
     *
     * @param apiKey The API key to use for the Google Places Autocomplete API.
     * @param block A block to configure the parameters for the Google Places Autocomplete API.
     */
    public constructor(
        apiKey: String,
        block: GooglePlacesParametersBuilder.() -> Unit,
    ) : this(apiKey, googlePlacesParameters(block))

    override fun url(param: String): String {
        val encodedQuery = param.encodeURLQueryComponent()
        return GooglePlacesAutocompleteService
            .search(encodedQuery, apiKey, sessionToken, parameters)
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
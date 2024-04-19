package dev.jordond.compass.autocomplete.web

import dev.jordond.compass.autocomplete.web.parameter.GooglePlacesParameters
import dev.jordond.compass.tools.web.HttpApiEndpoint
import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json

public interface GooglePlacesAutocompleteService : HttpAutocompleteService {

    public companion object {

        private const val BASE_URL = "maps.googleapis.com/maps/api/place/autocomplete/json"

        private fun createUrl(
            target: String,
            apiKey: String,
            sessionToken: String,
            params: GooglePlacesParameters = GooglePlacesParameters(),
        ): String = "$BASE_URL?$target&${params.encode()}&key=$apiKey&sessiontoken=$sessionToken"

        internal fun search(
            query: String,
            apiKey: String,
            sessionToken: String,
            params: GooglePlacesParameters,
        ) = createUrl(target = "input=$query", apiKey, sessionToken, params)

        internal fun details(
            placeId: String,
            apiKey: String,
            sessionToken: String,
        ) = createUrl(target = "place_id=$placeId", apiKey, sessionToken)
    }
}

public fun GooglePlacesAutocompleteService(
    apiKey: String,
    parameters: GooglePlacesParameters = GooglePlacesParameters(),
    json: Json = HttpApiEndpoint.json(),
    client: HttpClient = HttpApiEndpoint.httpClient(json),
): GooglePlacesAutocompleteService {
    val delegate = HttpAutocompleteService(
        searchEndpoint = GooglePlacesSearchEndpoint(apiKey, parameters),
        json = json,
        client = client,
    )
    return object : HttpAutocompleteService by delegate, GooglePlacesAutocompleteService {}
}
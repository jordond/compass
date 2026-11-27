package dev.jordond.compass.geocoder

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

internal class MockHttp(
    status: HttpStatusCode = HttpStatusCode.OK,
    body: String,
    expectSuccess: Boolean = true,
) {

    val requests: MutableList<HttpRequestData> = mutableListOf()

    val client: HttpClient = HttpClient(
        MockEngine { request ->
            requests += request
            respond(
                content = body,
                status = status,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        },
    ) {
        this.expectSuccess = expectSuccess
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
    }
}

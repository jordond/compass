# 📈 Web API Service

If you need to support more platforms other than Android/iOS, you can use a web based API service. Out of the box Compass supports [google-maps.md](google-maps.md "mention") and [Broken link](broken-reference "mention").

{% hint style="info" %}
In order to use these third party API's you will need to get an API key and also be aware of the costs associated with using their service.
{% endhint %}

### Custom API

If you aren't using the provided services, you can create your own by implementing your own `HttpApiPlatformGeocoder`.

#### Import the library

First you need to import the `geocoding-web` artifact:

```kts
implementation(libs.compass.geocoding.web)
```

#### Create your endpoints

Now you need to implement a `ForwardEndpoint` and `ReverseEndpoint` which are defined as:

```kotlin
public typealias ForwardEndpoint = HttpApiEndpoint<String, List<Coordinates>>
public typealias ReverseEndpoint = HttpApiEndpoint<Coordinates, List<Place>>
```

Here is an example `ForwardEndpoint`:

```kotlin
public class MyForwardEndpoint : ForwardEndpoint {

    override fun url(param: String): String {
        return "https://my-api.com/api/geocode?query=$param"
    }

    override suspend fun mapResponse(response: HttpResponse): List<Coordinates> {
        val result = response.body<MyAPIResponse>().resultsOrThrow()
        // Map the response to the Coordinates object
        return result.toCoordinates()
    }
}
```

Here you can use any API you want by creating the URL and mapping the response.

You can also use the helper extension function for more concise code:

```kotlin
val forwardEndpoint = ForwardEndpoint(
    url = { param -> "https://my-api.com/api/geocode?query=$param" },
    mapResponse = { response -> 
        response.body<MyAPIResponse>().resultsOrThrow().toCoordinates() 
    },
)
```

Now you can do the same for the `ReverseEndpoint` as well.

{% hint style="info" %}
The HTTP request is handled automatically for you. You can customize (to add authentication, headers, etc) this when you go to create the `HttpApiPlatformGeocoder` object, see below for more.
{% endhint %}

#### Create the Geocoder

In order to create the `Geocoder` object you need a `PlatformGeocoder` that can make the requests. In this case we need to create a `HttpApiPlatformGeocoder` this can be done like so:

```kotlin
val platformGeocoder = HttpApiPlatformGeocoder(
    forwardEndpoint = forwardEndpoint, // created above
    reverseEndpoint = reverseEndpoint,
)
```

Then you can finally create the `Geocoder`:

```kotlin
val geocoder = Geocoder(platformGeocoder)
geocoder.forward("London UK")
```

You can skip the `HttpApiPlatformGeocoder` step by using another extension function:

```kotlin
val geocoder = Geocoder(
    forwardEndpoint = forwardEndpoint, // created above
    reverseEndpoint = reverseEndpoint,
)
geocoder.forward("London UK")
```

#### Customizing the HTTP request

If you need to customize the HTTP request to your API then you can do so by passing a `HttpClient` or `Json` object to the above functions.\
\
Here is the signature for the above `Geocoder` function:

```kotlin
public fun Geocoder(
    forwardEndpoint: ForwardEndpoint,
    reverseEndpoint: ReverseEndpoint,
    json: Json = HttpApiEndpoint.json(),
    httpClient: HttpClient = HttpApiEndpoint.httpClient(json),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
): Geocoder 
```

Notice that a default `HttpClient` is used, but you can pass your own in.

# 📈 Web API Service

Compass supports Autocomplete on all platforms using the Geocoding services provided by Mapbox and
Google Maps.

## Getting started

To get started you first need to create an autocomplete instance:

```kotlin
val autocomplete = Autocomplete.googleMapsGeocoder(apiKey = "your-api-key")
```

Then you can use the `autocomplete` instance:

```kotlin
val results = mutableListOf<String>()

suspend fun search(query: String) {
    val result = autocomplete.getOrNull() ?: return
    if (result.isNotEmpty()) {
        results.clear()
        results.addAll(result)
    }
}
```
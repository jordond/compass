# Mapbox

To use [Mapbox API ](https://docs.mapbox.com/api/search/geocoding/)as your geocoding backend you will need to do the following:

### Obtain an API key

Create an account for [Mapbox](https://account.mapbox.com/), then copy your access token.

### Create the Geocoder

Simple pass your API key to the `Geocoder` function:

```kotlin
val geocoder = MapboxGeocoder(apiKey = "your-api-key")

// Or this helper function
val geocoder = Geocoder(apiKey = "your-api-key")
```

#### Customizing Google Maps request

The Mapbox geocoding API has some [optional parameters.](https://docs.mapbox.com/api/search/geocoding/#forward-geocoding-with-search-text-input) You can modify these by passing them into the `Geocoder` function:

```kotlin
val geocoder = MapboxGeocoder(apiKey = "your-api-key") {
    limit = 3
}
```

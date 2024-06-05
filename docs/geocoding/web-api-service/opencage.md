# OpenCage

To use [OpenCage API](https://opencagedata.com/api) as your geocoding backend you will need to do
the following:

### Obtain an API key

Create an account for [OpenCage](https://opencagedata.com/dashboard), then copy your access token.

### Create the Geocoder

Simple pass your API key to the `Geocoder` function:

```kotlin
val geocoder = OpenCageGeocoder(apiKey = "your-api-key")

// Or this helper function
val geocoder = Geocoder(apiKey = "your-api-key")
```

#### Customizing Google Maps request

The OpenCage geocoding API has
some [optional parameters.](https://opencagedata.com/api#optional-params) You can modify these by
passing them into the `Geocoder` function:

```kotlin
val geocoder = OpenCageGeocoder(apiKey = "your-api-key") {
    limit = 3
    noRecord = true
}
```

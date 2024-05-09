# Google Maps

To use[ Google Maps API](https://developers.google.com/maps/documentation/geocoding/overview) as your geocoding backend you will need to do the following:

### Obtain an API key

1. First you will need to [create a cloud project and enable the API](https://developers.google.com/maps/documentation/geocoding/cloud-setup)
2. Create the [API key](https://developers.google.com/maps/documentation/geocoding/get-api-key)
3. Copy your key

### Create the Geocoder

Simple pass your API key to the `Geocoder` function:

```kotlin
val geocoder = GoogleMapsGeocoder(apiKey = "your-api-key")

// Or this helper function
val geocoder = Geocoder(apiKey = "your-api-key")
```

#### Customizing Google Maps request

The Google Maps geocoding API has some [optional parameters](https://developers.google.com/maps/documentation/geocoding/requests-geocoding). You can modify these by passing them into the `Geocoder` function:

```kotlin
val geocoder = GoogleMapsGeocoder(apiKey = "your-api-key") {
    locationTypes(GoogleMapsLocationType.GeometricCenter)
}
```

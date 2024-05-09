# 📱 Android / iOS

Compass supports Autocomplete natively on Android and iOS by using the built in Geocoding services.

{% hint style="info" %}
If your project supports both Mobile and other targets, you need to configure your setup to provide
a `Autocomplete<Place>` for each platform. Check
out [mixed-platforms.md](../usage/mixed-platforms.md "mention")for more information.
{% endhint %}

## Getting started

To get started with Autocomplete on Android and iOS, you need to create an instance
of `Autocomplete<Place>`:

```kotlin
val autocomplete = Autocomplete.mobile()
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

{% hint style="info" %}
The Mobile Autocomplete uses the device's built-in Geocoding services to provide the results. These
results can vary in quality and quantity.
{% endhint %}
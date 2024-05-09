# 🔍 Overview

Autocomplete is used to provide real-time suggestions for locations as the user types in a query.

Compass currently provides an interface for interacting with a geocoding service, such as Mapbox.
The
autocomplete feature provides real-time suggestions to the user as they type an address or location
into a search field.

The library is structured around several key components:

1. `AutocompleteService`: This is an interface that represents a service providing autocomplete
   suggestions. It has a `search(query: String)` function, which searches for autocomplete
   suggestions based on the provided
   query.

2. `AutocompleteResult`: This is a sealed class that represents the result of an autocomplete
   operation. It can either be a `Success` with a list of data or an `Error` with a specific error
   message.

3. `Autocomplete`: This is a function that creates a new instance of `AutocompleteService` that uses
   a HTTP service to provide autocomplete suggestions.

{% hint style="info" %}
The current implementations of the `AutocompleteService` use the Geocoding artifacts from Compass
and perform a forward geocode operation. That means that the results may not be the highest of
quality.
{% endhint %}

### Quick start

```kotlin
suspend fun autocomplete(query: String): List<Place> {
   val autocomplete = AutoComplete.mobile()
   return autocomplete.search("London").getOrNull()
}
```

In this quick example the `AutoComplete.mobile()()` function is a convenience function for creating
a `AutoComplete<Place>` object. Each of the `autocomplete-*` artifacts provide one.

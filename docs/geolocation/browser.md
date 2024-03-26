# 🖥️ Browser

Compass supports [HTML5 Geolocation API](https://developer.mozilla.org/en-US/docs/Web/API/Geolocation\_API) with the `compass-geolocator-browser` artifact. The artifact supports JS and WASM targets.

Geolocation in the browser requires the user to provide permission. This is handled automatically when you start tracking or attempt to get the current location.

{% hint style="info" %}
If your project supports both Browser and other targets, you need to configure your setup to provide a `Geolocator` for each platform. Check out [mixed-platforms.md](../usage/mixed-platforms.md "mention")for more information.
{% endhint %}

## Usage

Follow these steps to create a `Geolocator`

### Create Locator

Geolocator is powered by the `Locator` object:

```kotlin
val locator: Locator = createBrowserLocator()
val locator: Locator = Locator.browser()
```

### Create Geolocator

Then you can use that to create the `Geolocator` object:

```kotlin
val geolocator: Geolocator = Geolocator(locator)
```

Or you can use an extension function to skip the `Locator` step:

```kotlin
val geolocator: Geolocator = Geolocator()
val geolocator: Geolocator = BrowserGeolocator()
val geolocator: Geolocator = Geolocator.browser()
```

## Permissions

Nothing extra is required to setup permissions for the browser.

When you attempt to get the location, the browser will show a prompt to the user. They can allow, deny, or do one of those permanently.

## Get location

Now you can follow the steps in [geolocator.md](geolocator.md "mention")

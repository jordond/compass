package dev.jordond.compass.geolocation.browser

import dev.jordond.compass.geolocation.Locator
import dev.jordond.compass.geolocation.browser.internal.DefaultBrowserLocator

/**
 * A locator that uses a web browser to determine the current location.
 */
public interface BrowserLocator : Locator

/**
 * Creates a new [BrowserLocator] that uses a web browser to determine the current location.
 *
 * [MDN Reference](https://developer.mozilla.org/en-US/docs/Web/API/Geolocation)
 *
 * @return A new [BrowserLocator].
 */
public fun createBrowserLocator(): BrowserLocator = DefaultBrowserLocator()

/**
 * Creates a new [BrowserLocator] that uses a web browser to determine the current location.
 *
 * [MDN Reference](https://developer.mozilla.org/en-US/docs/Web/API/Geolocation)
 *
 * @return A new [BrowserLocator].
 */
public fun Locator.Companion.browser(): BrowserLocator = createBrowserLocator()


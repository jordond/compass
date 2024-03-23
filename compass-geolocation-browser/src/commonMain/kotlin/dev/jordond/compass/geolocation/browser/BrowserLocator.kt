package dev.jordond.compass.geolocation.browser

import dev.jordond.compass.geolocation.Locator
import dev.jordond.compass.geolocation.browser.internal.DefaultBrowserLocator

public interface BrowserLocator : Locator

public fun createBrowserLocator(): BrowserLocator = DefaultBrowserLocator()

public fun Locator.Companion.browser(): BrowserLocator = createBrowserLocator()


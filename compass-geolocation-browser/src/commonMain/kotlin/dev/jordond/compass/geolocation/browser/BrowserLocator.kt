package dev.jordond.compass.geolocation.browser

import dev.jordond.compass.geolocation.Locator
import dev.jordond.compass.geolocation.browser.internal.DefaultBrowserLocator

public interface BrowserLocator : Locator

public fun createBrowserLocator(): dev.jordond.compass.geolocation.browser.BrowserLocator =
    DefaultBrowserLocator()

public fun Locator.Companion.browser(): dev.jordond.compass.geolocation.browser.BrowserLocator =
    createBrowserLocator()


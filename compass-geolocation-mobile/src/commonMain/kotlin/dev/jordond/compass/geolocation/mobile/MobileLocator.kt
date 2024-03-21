package dev.jordond.compass.geolocation.mobile

import dev.jordond.compass.geolocation.Locator

public interface MobileLocator : Locator

internal expect fun createLocator(): MobileLocator
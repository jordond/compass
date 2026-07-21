package dev.jordond.compass.geolocation.mobile.internal

import dev.jordond.compass.Location

/**
 * Milliseconds since the Unix epoch.
 *
 * Wall clock on purpose. It is only ever compared against [Location.timestampMillis], which the
 * platforms also stamp from the wall clock, so the two have to come from the same source. Anything
 * measuring elapsed time wants a monotonic reading instead.
 */
internal expect fun currentTimeMillis(): Long

package dev.jordond.compass.geolocation.mobile.internal

import platform.Foundation.NSDate
import platform.Foundation.NSProcessInfo
import platform.Foundation.timeIntervalSince1970

internal actual fun currentTimeMillis(): Long = (NSDate().timeIntervalSince1970 * 1000).toLong()

/**
 * Milliseconds the system has been awake since it last booted.
 *
 * Unlike [currentTimeMillis] this only ever moves forward, which is what measuring elapsed time
 * needs: an NTP correction or a user changing the clock moves the wall clock backwards, and a
 * check written against it then stalls until real time has caught up again.
 */
internal fun monotonicMillis(): Long = (NSProcessInfo.processInfo.systemUptime * 1000).toLong()

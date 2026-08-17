package dev.jordond.compass.permissions.mobile.internal

import dev.jordond.compass.permissions.LocationAccuracy

/**
 * Resolve the accuracy the app currently holds from the two things both platforms can report.
 *
 * Kept platform agnostic, and away from the platform types, so the branching can be tested on a
 * host instead of only on a device.
 *
 * @param granted Whether the app holds the location permission at all.
 * @param fullAccuracy Whether the permission it holds is the precise one, or `null` when the
 * platform draws no such distinction. `null` resolves to [LocationAccuracy.Full] rather than
 * [LocationAccuracy.Unknown]: a platform that cannot report reduced accuracy cannot grant it
 * either, so a granted permission there is always the precise one.
 */
internal fun resolveGrantedAccuracy(granted: Boolean, fullAccuracy: Boolean?): LocationAccuracy =
    when {
        // No permission at all, so there is no accuracy to describe. Reporting `Reduced` here would
        // read as "you hold something coarse", which is a worse lie than admitting nothing is held.
        !granted -> LocationAccuracy.Unknown
        fullAccuracy == false -> LocationAccuracy.Reduced
        else -> LocationAccuracy.Full
    }

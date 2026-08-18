package dev.jordond.compass.permissions

/**
 * Describes how precise the locations the app is allowed to read are.
 *
 * This is separate from [PermissionState] because the two answer different questions. A granted
 * permission says the app may read a location at all, this says how good that location is allowed
 * to be. On both platforms the user can grant the permission while withholding precision, so
 * [PermissionState.Granted] on its own does not mean the app holds [Full] accuracy.
 */
public enum class LocationAccuracy {

    /**
     * The app may read precise locations.
     *
     * Android holds `ACCESS_FINE_LOCATION`, iOS reports full accuracy authorization.
     */
    Full,

    /**
     * The app may only read approximate locations, on the order of a few kilometres.
     *
     * Android holds `ACCESS_COARSE_LOCATION` but not `ACCESS_FINE_LOCATION`, iOS reports reduced
     * accuracy authorization because the user turned Precise Location off.
     *
     * A requested accuracy finer than this is clamped by the system, so a
     * [dev.jordond.compass.Priority.HighAccuracy] request will still come back coarse.
     */
    Reduced,

    /**
     * The accuracy is not known.
     *
     * Either the app holds no location permission at all, or whatever was asked does not report
     * accuracy. It is never a claim that the accuracy is poor, only that there is nothing to say
     * about it.
     */
    Unknown,
}

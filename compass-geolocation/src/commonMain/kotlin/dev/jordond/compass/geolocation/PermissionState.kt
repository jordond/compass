package dev.jordond.compass.geolocation

/**
 * Describes the state of the location permission.
 */
public enum class PermissionState {

    /**
     * The permission has not been determined.
     */
    NotDetermined,

    /**
     * The permission has been granted.
     */
    Granted,

    /**
     * The permission has been denied.
     */
    Denied,

    /**
     * The permission has been denied forever.
     *
     * This means the user has denied the permission and selected "Don't ask again". The user must
     * manually enable the permission in the settings.
     */
    DeniedForever,
}
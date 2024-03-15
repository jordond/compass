package dev.jordond.compass

import dev.drewhamilton.poko.Poko

@Poko
public class Location(
    public val address: String?,
    public val city: String?,
    public val state: String?,
    public val postalCode: String?,
    public val country: String?,
    public val latitude: Double,
    public val longitude: Double,
)
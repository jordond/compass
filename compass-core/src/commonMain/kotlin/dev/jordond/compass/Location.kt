package dev.jordond.compass

import dev.drewhamilton.poko.Poko

@Poko
public class Location(
    public val coordinates: Coordinates,
    public val accuracy: Meters,
)
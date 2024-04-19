package dev.jordond.compass.autocomplete.web.parameter

import dev.drewhamilton.poko.Poko
import dev.jordond.compass.tools.web.parameter.QueryParameters
import dev.jordond.compass.tools.web.parameter.parametersOf

@Poko
public class GooglePlacesParameters(
    public val radius: Long? = null,
) : QueryParameters {

    override val parameters: Map<String, String> = parametersOf(
        "radius" to radius,
    )
}

public class GooglePlacesParametersBuilder {
    private var radius: Long? = null

    public fun radius(radius: Long): GooglePlacesParametersBuilder = apply {
        this.radius = radius
    }

    public fun build(): GooglePlacesParameters = GooglePlacesParameters(
        radius = radius,
    )
}

public fun googlePlacesParameters(
    block: GooglePlacesParametersBuilder.() -> Unit,
): GooglePlacesParameters = GooglePlacesParametersBuilder().apply(block).build()
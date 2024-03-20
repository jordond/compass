package dev.jordond.compass.geocoder.web.parameter

import dev.drewhamilton.poko.Poko

/**
 * Options for the TEMPLATE geocoder.
 *
 * See the []()
 * for more information.
 */
@Poko
public class TemplateParameters(
    public val foo: String? = null,
) : QueryParameters {

    override val parameters: Map<String, String> = parametersOf(
        "foo" to foo,
    )
}

/**
 * A builder class to build [TemplateParameters].
 */
@Suppress("MemberVisibilityCanBePrivate")
public class TemplateParametersBuilder : QueryParametersBuilder<TemplateParameters> {

    /**
     * @see TemplateParameters.locationType
     */
    private var foo: String? = null

    /**
     * Build the instance of [TemplateParameters].
     */
    override fun build(): TemplateParameters = TemplateParameters(
        foo = foo,
    )
}

/**
 * Create a [TemplateParameters] instance with the provided [block].
 *
 * @param block The builder block to build the instance of [TemplateParameters].
 * @return The instance of [TemplateParameters].
 */
public fun templateParameters(
    block: TemplateParametersBuilder.() -> Unit,
): TemplateParameters = TemplateParametersBuilder().apply(block).build()
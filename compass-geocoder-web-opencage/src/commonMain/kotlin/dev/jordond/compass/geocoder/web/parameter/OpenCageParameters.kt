package dev.jordond.compass.geocoder.web.parameter

import dev.drewhamilton.poko.Poko
import dev.jordond.compass.tools.web.parameter.QueryParameters
import dev.jordond.compass.tools.web.parameter.QueryParametersBuilder
import dev.jordond.compass.tools.web.parameter.parametersOf

/**
 * Options for the OpenCage geocoder.
 *
 * See the [OpenCage](https://opencagedata.com/api#optional-params)
 * for more information.
 *
 * @param limit The maximum number of results we should return. Maximum allowable value is 100.
 * @param countryCode Used only for forward geocoding. Restricts results to the specified
 * country/territory or countries. The country code is a two letter code as defined by the
 * ISO 3166-1 Alpha 2 standard. E.g. `gb` for the United Kingdom, `fr` for France, `us` for
 * United States. Non-two letter country codes are ignored.
 * @param noDedupe If set to `false`, the results will not be deduplicated.
 * @param noRecord When set to `true` the query contents are not logged. Please use this parameter
 * if you have concerns about privacy, and want us to have no record of your query. Learn more
 * about OpenCage's [approach to privacy](https://opencagedata.com/api#privacy).
 */
@Poko
public class OpenCageParameters(
    public val limit: Int = PARAM_DEFAULT_LIMIT,
    public val countryCode: List<String> = emptyList(),
    public val noDedupe: Boolean = false,
    public val noRecord: Boolean = false,
) : QueryParameters {

    override val parameters: Map<String, String> = parametersOf(
        "limit" to limit,
        "countrycode" to countryCode,
        "no_dedupe" to noDedupe.toInt(),
        "no_record" to noRecord.toInt(),
    )

    public companion object {

        internal const val PARAM_DEFAULT_LIMIT = 10
    }
}

/**
 * A builder class to build [OpenCageParameters].
 */
@Suppress("MemberVisibilityCanBePrivate")
public class OpenCageParametersBuilder : QueryParametersBuilder<OpenCageParameters> {

    /**
     * @see OpenCageParameters.limit
     */
    public var limit: Int = OpenCageParameters.PARAM_DEFAULT_LIMIT

    /**
     * @see OpenCageParameters.countryCode
     */
    private var countryCode: List<String> = emptyList()

    /**
     * @see OpenCageParameters.countryCode
     */
    public fun countryCodes(codes: List<String>) {
        this.countryCode = codes
    }

    /**
     * @see OpenCageParameters.countryCode
     */
    public fun countryCode(vararg countryCode: String) {
        this.countryCode = countryCode.toList()
    }

    /**
     * @see OpenCageParameters.dedupe
     */
    public var noDedupe: Boolean = false

    /**
     * @see OpenCageParameters.noRecord
     */
    public var noRecord: Boolean = false

    /**
     * Build the instance of [OpenCageParameters].
     */
    override fun build(): OpenCageParameters = OpenCageParameters(
        limit = limit,
        countryCode = countryCode,
        noDedupe = noDedupe,
        noRecord = noRecord,
    )
}

/**
 * Create a [OpenCageParameters] instance with the provided [block].
 *
 * @param block The builder block to build the instance of [OpenCageParameters].
 * @return The instance of [OpenCageParameters].
 */
public fun openCageParameters(
    block: OpenCageParametersBuilder.() -> Unit,
): OpenCageParameters = OpenCageParametersBuilder().apply(block).build()
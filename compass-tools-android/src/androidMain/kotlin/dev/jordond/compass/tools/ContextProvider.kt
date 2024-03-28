package dev.jordond.compass.tools

import android.annotation.SuppressLint
import android.content.Context
import dev.jordond.compass.InternalCompassApi

/**
 * Class for providing the application context.
 */
@InternalCompassApi
public class ContextProvider(public val context: Context) {

    @InternalCompassApi
    public companion object {

        @SuppressLint("StaticFieldLeak")
        private var instance: ContextProvider? = null

        public fun create(context: Context): ContextProvider {
            if (instance == null) {
                instance = ContextProvider(context)
            }

            return instance!!
        }

        public fun getInstance(): ContextProvider = instance
            ?: throw IllegalStateException("ContextProvider has not been initialized")
    }
}
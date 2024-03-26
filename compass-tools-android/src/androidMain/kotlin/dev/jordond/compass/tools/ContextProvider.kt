package dev.jordond.compass.tools

import android.annotation.SuppressLint
import android.content.Context

/**
 * Class for providing the application context.
 */
public class ContextProvider(public val context: Context) {

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
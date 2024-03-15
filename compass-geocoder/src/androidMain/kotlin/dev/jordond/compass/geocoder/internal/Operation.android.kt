package dev.jordond.compass.geocoder.internal

import android.annotation.TargetApi
import android.location.Address
import android.location.Geocoder
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

internal suspend fun syncOperation(
    block: () -> MutableList<Address>?,
): List<Address> {
    val result: Result<List<Address>?> = withContext(Dispatchers.IO) {
        runCatching {
            block()
        }
    }

    val exception = result.exceptionOrNull()
    if (exception != null) throw GeocodeError(exception.message)

    return result.getOrNull() ?: emptyList()
}

@TargetApi(Build.VERSION_CODES.TIRAMISU)
internal suspend fun asyncOperation(
    block: (listener: Geocoder.GeocodeListener) -> Unit,
): List<Address> {
    return suspendCoroutine { continuation ->
        val listener = object : Geocoder.GeocodeListener {
            override fun onGeocode(addresses: MutableList<Address>) {
                continuation.resume(addresses.toList())
            }

            override fun onError(errorMessage: String?) {
                continuation.resumeWithException(GeocodeError(errorMessage))
            }
        }

        block(listener)
    }
}
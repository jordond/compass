package dev.jordond.compass.geocoder.internal

import android.annotation.TargetApi
import android.location.Address
import android.location.Geocoder
import android.os.Build
import dev.jordond.compass.Place
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

internal suspend fun syncOperation(
    block: () -> MutableList<Address>?,
): List<Place> {
    val result: Result<List<Address>?> = withContext(Dispatchers.IO) {
        runCatching {
            block()
        }
    }

    val exception = result.exceptionOrNull()
    if (exception != null) throw GeocodeError(exception.message)

    return result.getOrNull()?.map { it.toPlace() } ?: emptyList()
}

@TargetApi(Build.VERSION_CODES.TIRAMISU)
internal suspend fun asyncOperation(
    block: (listener: Geocoder.GeocodeListener) -> Unit,
): List<Place> {
    return suspendCoroutine { continuation ->
        val listener = object : Geocoder.GeocodeListener {
            override fun onGeocode(addresses: MutableList<Address>) {
                val places = addresses.map { it.toPlace() }
                continuation.resume(places)
            }

            override fun onError(errorMessage: String?) {
                continuation.resumeWithException(GeocodeError(errorMessage))
            }
        }

        block(listener)
    }
}
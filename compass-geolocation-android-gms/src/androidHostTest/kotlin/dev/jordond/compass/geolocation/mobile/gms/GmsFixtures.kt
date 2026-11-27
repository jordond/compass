package dev.jordond.compass.geolocation.mobile.gms

import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.tasks.OnCanceledListener
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import java.util.concurrent.Executor

/**
 * Makes Google Play Services answer every availability check with [result].
 *
 * Callers must undo the static mock with `unmockkAll`.
 *
 * @return The availability mock, for stubbing other answers or verifying the check.
 */
internal fun stubPlayServices(result: Int): GoogleApiAvailability {
    val availability = mockk<GoogleApiAvailability>()
    mockkStatic(GoogleApiAvailability::class)
    every { GoogleApiAvailability.getInstance() } returns availability
    every { availability.isGooglePlayServicesAvailable(any()) } returns result
    return availability
}

/**
 * A Play Services [Task] in a fixed state, readable both through its listeners and through the
 * completion getters that `await` checks first.
 *
 * @param complete `false` for a task that never finishes, which notifies no listener.
 */
internal fun <T> fakeTask(
    result: T? = null,
    failure: Exception? = null,
    cancelled: Boolean = false,
    complete: Boolean = true,
): Task<T> {
    val succeeded = complete && failure == null && !cancelled
    val task = mockk<Task<T>>()
    every { task.isComplete } returns complete
    every { task.isCanceled } returns cancelled
    every { task.exception } returns failure
    every { task.result } returns result
    every { task.addOnSuccessListener(any<OnSuccessListener<in T>>()) } answers {
        if (succeeded) firstArg<OnSuccessListener<T?>>().onSuccess(result)
        task
    }
    every { task.addOnCanceledListener(any<OnCanceledListener>()) } answers {
        if (complete && cancelled) firstArg<OnCanceledListener>().onCanceled()
        task
    }
    every { task.addOnFailureListener(any<OnFailureListener>()) } answers {
        if (complete && failure != null) firstArg<OnFailureListener>().onFailure(failure)
        task
    }
    every { task.addOnCompleteListener(any<Executor>(), any<OnCompleteListener<T>>()) } answers {
        if (complete) secondArg<OnCompleteListener<T>>().onComplete(task)
        task
    }
    return task
}

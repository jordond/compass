package dev.jordond.compass.geolocation.mobile.internal

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

/**
 * Queues [block] to run on the main thread.
 *
 * `CLLocationManager` delivers its delegate callbacks on the run loop of the thread that created
 * it. Kotlin/Native worker threads, such as the ones backing `Dispatchers.Default`, have no run
 * loop, so a manager created on one never calls back and every request suspends forever. Creating
 * and driving the manager from here keeps it bound to the main run loop no matter which dispatcher
 * the caller happens to be on.
 *
 * Callers already on the main thread are queued like everybody else rather than run inline.
 * Running inline would let a later call overtake an earlier one queued from a background thread,
 * so a `stopTracking()` could land after the `track()` that was meant to replace it, leaving
 * CoreLocation stopped while the bookkeeping said otherwise. Always queueing keeps the main
 * queue's ordering.
 *
 * Queueing is asynchronous on purpose. `dispatch_sync` would deadlock whenever the main thread is
 * already blocked waiting on the calling thread.
 */
internal fun onMainThread(block: () -> Unit) {
    dispatch_async(dispatch_get_main_queue(), block)
}

/**
 * Runs [block] on the main thread and suspends until it produces a result.
 *
 * The suspending counterpart to [onMainThread], for reads that have to hand a value back.
 * `Dispatchers.Main` queues onto the same main queue, so ordering against [onMainThread] holds.
 *
 * Suspending rather than blocking is what keeps this safe: the caller can be cancelled while it
 * waits, and no thread is held hostage in the meantime.
 */
internal suspend fun <T> awaitOnMainThread(block: () -> T): T =
    withContext(Dispatchers.Main) { block() }

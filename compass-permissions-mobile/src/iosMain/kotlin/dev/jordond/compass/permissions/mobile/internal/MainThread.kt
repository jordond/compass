package dev.jordond.compass.permissions.mobile.internal

import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

/**
 * Queues [block] to run on the main thread.
 *
 * `CLLocationManager` delivers its delegate callbacks on the run loop of the thread that created
 * it. Kotlin/Native worker threads, such as the ones backing `Dispatchers.Default`, have no run
 * loop, so a manager created on one never calls back and the permission request suspends forever.
 * Creating and driving the manager from here keeps it bound to the main run loop no matter which
 * dispatcher the caller happens to be on.
 *
 * Callers already on the main thread are queued like everybody else rather than run inline.
 * Running inline would let a later call overtake an earlier one queued from a background thread,
 * reordering the callback bookkeeping these blocks exist to protect.
 *
 * Queueing is asynchronous on purpose. `dispatch_sync` would deadlock whenever the main thread is
 * already blocked waiting on the calling thread.
 */
internal fun onMainThread(block: () -> Unit) {
    dispatch_async(dispatch_get_main_queue(), block)
}

package dev.jordond.compass.permissions.mobile.internal

import platform.Foundation.NSThread
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

/**
 * Runs [block] on the main thread, immediately if the caller is already on it.
 *
 * `CLLocationManager` delivers its delegate callbacks on the run loop of the thread that created
 * it. Kotlin/Native worker threads, such as the ones backing `Dispatchers.Default`, have no run
 * loop, so a manager created on one never calls back and the permission request suspends forever.
 * Creating and driving the manager from here keeps it bound to the main run loop no matter which
 * dispatcher the caller happens to be on.
 *
 * Dispatching is asynchronous on purpose. `dispatch_sync` would deadlock whenever the main thread
 * is already blocked waiting on the calling thread.
 */
internal fun onMainThread(block: () -> Unit) {
    if (NSThread.isMainThread()) block()
    else dispatch_async(dispatch_get_main_queue(), block)
}

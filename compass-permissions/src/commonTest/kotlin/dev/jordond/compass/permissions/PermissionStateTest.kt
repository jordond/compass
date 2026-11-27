package dev.jordond.compass.permissions

import dev.jordond.compass.permissions.exception.PermissionDeniedException
import dev.jordond.compass.permissions.exception.PermissionDeniedForeverException
import kotlin.test.Test
import kotlin.test.assertFailsWith

class PermissionStateTest {

    @Test
    fun grantedAndNotDeterminedDoNotThrow() {
        PermissionState.Granted.throwOnError()
        PermissionState.NotDetermined.throwOnError()
    }

    @Test
    fun deniedThrowsPermissionDenied() {
        assertFailsWith<PermissionDeniedException> { PermissionState.Denied.throwOnError() }
    }

    @Test
    fun deniedForeverThrowsPermissionDeniedForever() {
        assertFailsWith<PermissionDeniedForeverException> {
            PermissionState.DeniedForever.throwOnError()
        }
    }
}

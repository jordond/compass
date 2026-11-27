package dev.jordond.compass.permissions.exception

import kotlin.test.Test
import kotlin.test.assertEquals

class PermissionExceptionTest {

    @Test
    fun deniedMessageNamesTheDefaultPermission() {
        assertEquals("Permission Location was denied", PermissionDeniedException().message)
    }

    @Test
    fun deniedMessageNamesACustomPermission() {
        assertEquals("Permission Camera was denied", PermissionDeniedException("Camera").message)
    }

    @Test
    fun deniedForeverMessageSaysPermanently() {
        assertEquals(
            "Permission Location was denied permanently",
            PermissionDeniedForeverException().message,
        )
    }
}

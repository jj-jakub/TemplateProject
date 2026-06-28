package com.jj.templateproject.data.firebase

import android.content.Intent
import com.google.firebase.messaging.RemoteMessage
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class FirebaseExtrasUtilsTest {

    @Test
    fun `getAppPackageName returns the package name when present in the payload`() {
        val remoteMessage = mockk<RemoteMessage> {
            every { data } returns mapOf("appPackageName" to "com.example.target")
        }

        assertEquals("com.example.target", getAppPackageName(remoteMessage))
    }

    @Test
    fun `getAppPackageName returns null when the payload has no package name`() {
        val remoteMessage = mockk<RemoteMessage> {
            every { data } returns emptyMap()
        }

        assertNull(getAppPackageName(remoteMessage))
    }

    @Test
    fun `getAppPageOpeningIntent builds a market view intent for the package`() {
        val intent = getAppPageOpeningIntent("com.example.target")

        assertEquals(Intent.ACTION_VIEW, intent.action)
        assertEquals("market://details?id=com.example.target", intent.data.toString())
    }
}

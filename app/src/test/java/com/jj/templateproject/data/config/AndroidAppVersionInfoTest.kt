package com.jj.templateproject.data.config

import com.jj.templateproject.BuildConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AndroidAppVersionInfoTest {

    private val versionInfo = AndroidAppVersionInfo()

    @Test
    fun `reads the exact BuildConfig fields`() {
        assertEquals(BuildConfig.currentRevisionHash, versionInfo.revisionHash)
        assertEquals(BuildConfig.ciBuildNumber.toString(), versionInfo.buildNumber)
        assertEquals(BuildConfig.VERSION_NAME, versionInfo.versionName)
    }
}

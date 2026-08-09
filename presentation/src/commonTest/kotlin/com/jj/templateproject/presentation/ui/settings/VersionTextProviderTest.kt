package com.jj.templateproject.presentation.ui.settings

import com.jj.templateproject.presentation.FakeAppVersionInfo
import kotlin.test.Test
import kotlin.test.assertEquals

class VersionTextProviderTest {

    @Test
    fun `formats the exact revision build number and version name`() {
        val info = FakeAppVersionInfo(revisionHash = "abcd1234", buildNumber = "17", versionName = "1.0")

        val text = VersionTextProvider(info).getAboutVersionText()

        assertEquals("Revision: abcd1234, Build number: 17, Version: 1.0", text)
    }
}

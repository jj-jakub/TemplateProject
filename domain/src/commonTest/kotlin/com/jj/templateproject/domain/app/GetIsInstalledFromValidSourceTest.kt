package com.jj.templateproject.domain.app

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetIsInstalledFromValidSourceTest {

    @Test
    fun `returns true when the repository reports a valid source`() = runTest {
        val useCase = GetIsInstalledFromValidSource(FakeAppInfoRepository(installedFromValidSource = true))

        assertEquals(true, useCase())
    }

    @Test
    fun `returns false when the repository reports an invalid source`() = runTest {
        val useCase = GetIsInstalledFromValidSource(FakeAppInfoRepository(installedFromValidSource = false))

        assertEquals(false, useCase())
    }
}

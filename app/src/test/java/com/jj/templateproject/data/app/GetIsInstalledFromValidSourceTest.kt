package com.jj.templateproject.data.app

import com.jj.templateproject.domain.app.AppInfoRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GetIsInstalledFromValidSourceTest {

    private val appInfoRepository = mockk<AppInfoRepository>()
    private val useCase = GetIsInstalledFromValidSource(appInfoRepository)

    @Test
    fun `returns true when the repository reports a valid source`() = runTest {
        coEvery { appInfoRepository.installedFromValidSource() } returns true

        assertEquals(true, useCase())
    }

    @Test
    fun `returns false when the repository reports an invalid source`() = runTest {
        coEvery { appInfoRepository.installedFromValidSource() } returns false

        assertEquals(false, useCase())
    }

    @Test
    fun `delegates to the repository`() = runTest {
        coEvery { appInfoRepository.installedFromValidSource() } returns true

        useCase()

        coVerify(exactly = 1) { appInfoRepository.installedFromValidSource() }
    }
}

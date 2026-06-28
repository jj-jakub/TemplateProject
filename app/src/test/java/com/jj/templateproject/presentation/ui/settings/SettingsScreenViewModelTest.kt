package com.jj.templateproject.presentation.ui.settings

import com.jj.templateproject.data.app.GetIsInstalledFromValidSource
import com.jj.templateproject.data.config.VersionTextProvider
import com.jj.templateproject.domain.BaseResult
import com.jj.templateproject.domain.google.GetGoogleDataUseCase
import com.jj.templateproject.domain.google.GetGoogleStatusUseCase
import com.jj.templateproject.domain.google.exception.NetworkError
import com.jj.templateproject.util.MainDispatcherExtension
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class SettingsScreenViewModelTest {

    @JvmField
    @RegisterExtension
    val mainDispatcher = MainDispatcherExtension()

    private val versionTextProvider = mockk<VersionTextProvider>()
    private val getGoogleStatusUseCase = mockk<GetGoogleStatusUseCase>()
    private val getGoogleDataUseCase = mockk<GetGoogleDataUseCase>()
    private val getIsInstalledFromValidSource = mockk<GetIsInstalledFromValidSource>()

    private fun createViewModel() = SettingsScreenViewModel(
        versionTextProvider = versionTextProvider,
        getGoogleStatusUseCase = getGoogleStatusUseCase,
        getGoogleDataUseCase = getGoogleDataUseCase,
        getIsInstalledFromValidSource = getIsInstalledFromValidSource,
    )

    @Test
    fun `version text is taken from the provider`() {
        every { versionTextProvider.getAboutVersionText() } returns "Revision: abc"
        coEvery { getGoogleStatusUseCase.invoke() } returns BaseResult.Success(Unit)
        coEvery { getGoogleDataUseCase.invoke() } returns BaseResult.Success("200")
        coEvery { getIsInstalledFromValidSource.invoke() } returns true

        val viewModel = createViewModel()

        assertEquals("Revision: abc", viewModel.viewState.value.versionText)
    }

    @Test
    fun `successful api calls expose Ok status, data and stop loading`() {
        every { versionTextProvider.getAboutVersionText() } returns "v"
        coEvery { getGoogleStatusUseCase.invoke() } returns BaseResult.Success(Unit)
        coEvery { getGoogleDataUseCase.invoke() } returns BaseResult.Success("200")
        coEvery { getIsInstalledFromValidSource.invoke() } returns true

        val viewModel = createViewModel()
        val state = viewModel.viewState.value

        assertEquals("Ok", state.apiCallStatus)
        assertEquals("200", state.apiCallData)
        assertEquals(true, state.installedFromValidSource)
        assertFalse(state.loading)
    }

    @Test
    fun `status error surfaces the error message and data error shows Error`() {
        every { versionTextProvider.getAboutVersionText() } returns "v"
        coEvery { getGoogleStatusUseCase.invoke() } returns
            BaseResult.Error(NetworkError(500, "server down"))
        coEvery { getGoogleDataUseCase.invoke() } returns
            BaseResult.Error(NetworkError(500, "server down"))
        coEvery { getIsInstalledFromValidSource.invoke() } returns false

        val viewModel = createViewModel()
        val state = viewModel.viewState.value

        assertEquals("server down", state.apiCallStatus)
        assertEquals("Error", state.apiCallData)
        assertEquals(false, state.installedFromValidSource)
        assertFalse(state.loading)
    }

    @Test
    fun `no runtime permission is required below tiramisu`() {
        every { versionTextProvider.getAboutVersionText() } returns "v"
        coEvery { getGoogleStatusUseCase.invoke() } returns BaseResult.Success(Unit)
        coEvery { getGoogleDataUseCase.invoke() } returns BaseResult.Success("200")
        coEvery { getIsInstalledFromValidSource.invoke() } returns true

        assertTrue(createViewModel().viewState.value.requiredPermissions.isEmpty())
    }
}

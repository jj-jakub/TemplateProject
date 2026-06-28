package com.jj.templateproject.presentation.ui.settings

import com.jj.templateproject.data.app.GetIsInstalledFromValidSource
import com.jj.templateproject.data.config.VersionTextProvider
import com.jj.templateproject.domain.BaseResult
import com.jj.templateproject.domain.google.GetGoogleDataUseCase
import com.jj.templateproject.domain.google.GetGoogleStatusUseCase
import com.jj.templateproject.domain.google.exception.NetworkError
import com.jj.templateproject.domain.theme.GetThemeModeUseCase
import com.jj.templateproject.domain.theme.SetThemeModeUseCase
import com.jj.templateproject.domain.theme.ThemeMode
import com.jj.templateproject.presentation.ui.settings.model.ApiData
import com.jj.templateproject.presentation.ui.state.UiState
import com.jj.templateproject.util.MainDispatcherExtension
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.jupiter.api.Assertions.assertEquals
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
    private val getThemeModeUseCase = mockk<GetThemeModeUseCase>()
    private val setThemeModeUseCase = mockk<SetThemeModeUseCase>(relaxed = true)

    private fun createViewModel(themeMode: ThemeMode = ThemeMode.SYSTEM): SettingsScreenViewModel {
        every { getThemeModeUseCase() } returns flowOf(themeMode)
        return SettingsScreenViewModel(
            versionTextProvider = versionTextProvider,
            getGoogleStatusUseCase = getGoogleStatusUseCase,
            getGoogleDataUseCase = getGoogleDataUseCase,
            getIsInstalledFromValidSource = getIsInstalledFromValidSource,
            getThemeModeUseCase = getThemeModeUseCase,
            setThemeModeUseCase = setThemeModeUseCase,
        )
    }

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
    fun `successful api calls expose a Success state with Ok status and data`() {
        every { versionTextProvider.getAboutVersionText() } returns "v"
        coEvery { getGoogleStatusUseCase.invoke() } returns BaseResult.Success(Unit)
        coEvery { getGoogleDataUseCase.invoke() } returns BaseResult.Success("200")
        coEvery { getIsInstalledFromValidSource.invoke() } returns true

        val viewModel = createViewModel()
        val state = viewModel.viewState.value

        assertEquals(UiState.Success(ApiData(status = "Ok", data = "200")), state.apiState)
        assertEquals(true, state.installedFromValidSource)
    }

    @Test
    fun `a status error surfaces an error state with the message`() {
        every { versionTextProvider.getAboutVersionText() } returns "v"
        coEvery { getGoogleStatusUseCase.invoke() } returns
            BaseResult.Error(NetworkError.Http(500, "server down"))
        coEvery { getGoogleDataUseCase.invoke() } returns BaseResult.Success("200")
        coEvery { getIsInstalledFromValidSource.invoke() } returns false

        val viewModel = createViewModel()
        val state = viewModel.viewState.value

        assertEquals(UiState.Error("server down"), state.apiState)
        assertEquals(false, state.installedFromValidSource)
    }

    @Test
    fun `a data error surfaces an error state with the message`() {
        every { versionTextProvider.getAboutVersionText() } returns "v"
        coEvery { getGoogleStatusUseCase.invoke() } returns BaseResult.Success(Unit)
        coEvery { getGoogleDataUseCase.invoke() } returns
            BaseResult.Error(NetworkError.Connectivity)
        coEvery { getIsInstalledFromValidSource.invoke() } returns true

        val state = createViewModel().viewState.value

        assertEquals(UiState.Error("No network connection"), state.apiState)
    }

    @Test
    fun `the persisted theme mode is observed into state`() {
        every { versionTextProvider.getAboutVersionText() } returns "v"
        coEvery { getGoogleStatusUseCase.invoke() } returns BaseResult.Success(Unit)
        coEvery { getGoogleDataUseCase.invoke() } returns BaseResult.Success("200")
        coEvery { getIsInstalledFromValidSource.invoke() } returns true

        val state = createViewModel(themeMode = ThemeMode.DARK).viewState.value

        assertEquals(ThemeMode.DARK, state.themeMode)
    }

    @Test
    fun `retry re-runs the fetch and recovers from an error`() {
        every { versionTextProvider.getAboutVersionText() } returns "v"
        coEvery { getIsInstalledFromValidSource.invoke() } returns true
        coEvery { getGoogleDataUseCase.invoke() } returns BaseResult.Success("200")
        coEvery { getGoogleStatusUseCase.invoke() } returns
            BaseResult.Error(NetworkError.Connectivity)

        val viewModel = createViewModel()
        assertEquals(UiState.Error("No network connection"), viewModel.viewState.value.apiState)

        coEvery { getGoogleStatusUseCase.invoke() } returns BaseResult.Success(Unit)
        viewModel.retry()

        assertEquals(
            UiState.Success(ApiData(status = "Ok", data = "200")),
            viewModel.viewState.value.apiState,
        )
        coVerify(exactly = 2) { getGoogleStatusUseCase.invoke() }
    }

    @Test
    fun `setThemeMode delegates to the use case`() {
        every { versionTextProvider.getAboutVersionText() } returns "v"
        coEvery { getGoogleStatusUseCase.invoke() } returns BaseResult.Success(Unit)
        coEvery { getGoogleDataUseCase.invoke() } returns BaseResult.Success("200")
        coEvery { getIsInstalledFromValidSource.invoke() } returns true

        createViewModel().setThemeMode(ThemeMode.LIGHT)

        coVerify(exactly = 1) { setThemeModeUseCase(ThemeMode.LIGHT) }
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

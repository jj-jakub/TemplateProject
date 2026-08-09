package com.jj.templateproject.presentation.ui.settings

import com.jj.templateproject.domain.BaseResult
import com.jj.templateproject.domain.app.GetIsInstalledFromValidSource
import com.jj.templateproject.domain.google.GetGoogleDataUseCase
import com.jj.templateproject.domain.google.GetGoogleStatusUseCase
import com.jj.templateproject.domain.google.exception.NetworkError
import com.jj.templateproject.domain.theme.GetThemeModeUseCase
import com.jj.templateproject.domain.theme.SetThemeModeUseCase
import com.jj.templateproject.domain.theme.ThemeMode
import com.jj.templateproject.presentation.FakeAppPreferencesRepository
import com.jj.templateproject.presentation.FakeAppVersionInfo
import com.jj.templateproject.presentation.ui.settings.model.ApiData
import com.jj.templateproject.presentation.ui.state.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Built on real use cases over hand-written fakes at the repository boundary
 * ([FakeTemplateRepository], [FakeAppInfoRepository], [FakeAppPreferencesRepository]) rather than
 * mocked use cases: the use cases themselves are one-line delegations with nothing of their own
 * worth mocking, so faking one layer lower exercises the same behaviour more realistically.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SettingsScreenViewModelTest {

    @BeforeTest
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private val templateRepository = FakeTemplateRepository()
    private val appInfoRepository = FakeAppInfoRepository()
    private val appPreferencesRepository = FakeAppPreferencesRepository()

    private fun createViewModel(
        versionText: String = "v",
        themeMode: ThemeMode = ThemeMode.SYSTEM,
    ): SettingsScreenViewModel {
        // Reuse the shared fake for the common (SYSTEM) case; a non-default starting mode needs its
        // own instance, since FakeAppPreferencesRepository's theme mode is set at construction.
        val preferences = if (themeMode == ThemeMode.SYSTEM) {
            appPreferencesRepository
        } else {
            FakeAppPreferencesRepository(themeMode = themeMode)
        }
        return SettingsScreenViewModel(
            versionTextProvider = VersionTextProvider(FakeAppVersionInfo(versionName = versionText)),
            getGoogleStatusUseCase = GetGoogleStatusUseCase(templateRepository),
            getGoogleDataUseCase = GetGoogleDataUseCase(templateRepository),
            getIsInstalledFromValidSource = GetIsInstalledFromValidSource(appInfoRepository),
            getThemeModeUseCase = GetThemeModeUseCase(preferences),
            setThemeModeUseCase = SetThemeModeUseCase(preferences),
        )
    }

    @Test
    fun `version text is taken from the provider`() {
        val viewModel = createViewModel(versionText = "abc")

        assertTrue(viewModel.viewState.value.versionText.contains("abc"))
    }

    @Test
    fun `successful api calls expose a Success state with Ok status and data`() {
        templateRepository.statusResult = BaseResult.Success(Unit)
        templateRepository.dataResult = BaseResult.Success("200")
        appInfoRepository.installedFromValidSource = true

        val state = createViewModel().viewState.value

        assertEquals(UiState.Success(ApiData(status = "Ok", data = "200")), state.apiState)
        assertEquals(true, state.installedFromValidSource)
    }

    @Test
    fun `a status error surfaces an error state with the message`() {
        templateRepository.statusResult = BaseResult.Error(NetworkError.Http(500, "server down"))
        appInfoRepository.installedFromValidSource = false

        val state = createViewModel().viewState.value

        assertEquals(UiState.Error("server down"), state.apiState)
        assertEquals(false, state.installedFromValidSource)
    }

    @Test
    fun `a data error surfaces an error state with the message`() {
        templateRepository.statusResult = BaseResult.Success(Unit)
        templateRepository.dataResult = BaseResult.Error(NetworkError.Connectivity)

        val state = createViewModel().viewState.value

        assertEquals(UiState.Error("No network connection"), state.apiState)
    }

    @Test
    fun `the persisted theme mode is observed into state`() {
        val state = createViewModel(themeMode = ThemeMode.DARK).viewState.value

        assertEquals(ThemeMode.DARK, state.themeMode)
    }

    @Test
    fun `retry re-runs the fetch and recovers from an error`() {
        templateRepository.statusResult = BaseResult.Error(NetworkError.Connectivity)
        templateRepository.dataResult = BaseResult.Success("200")

        val viewModel = createViewModel()
        assertEquals(UiState.Error("No network connection"), viewModel.viewState.value.apiState)

        templateRepository.statusResult = BaseResult.Success(Unit)
        viewModel.retry()

        assertEquals(
            UiState.Success(ApiData(status = "Ok", data = "200")),
            viewModel.viewState.value.apiState,
        )
        assertEquals(2, templateRepository.statusCallCount)
    }

    @Test
    fun `setThemeMode writes through to the preferences repository`() {
        val preferences = FakeAppPreferencesRepository(themeMode = ThemeMode.SYSTEM)
        val viewModel = SettingsScreenViewModel(
            versionTextProvider = VersionTextProvider(FakeAppVersionInfo()),
            getGoogleStatusUseCase = GetGoogleStatusUseCase(templateRepository),
            getGoogleDataUseCase = GetGoogleDataUseCase(templateRepository),
            getIsInstalledFromValidSource = GetIsInstalledFromValidSource(appInfoRepository),
            getThemeModeUseCase = GetThemeModeUseCase(preferences),
            setThemeModeUseCase = SetThemeModeUseCase(preferences),
        )

        viewModel.setThemeMode(ThemeMode.LIGHT)

        assertEquals(ThemeMode.LIGHT, viewModel.viewState.value.themeMode)
    }
}

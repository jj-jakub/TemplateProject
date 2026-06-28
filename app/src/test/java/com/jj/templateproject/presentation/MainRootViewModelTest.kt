package com.jj.templateproject.presentation

import com.jj.templateproject.data.ad.GetMainAdUnitId
import com.jj.templateproject.domain.theme.GetThemeModeUseCase
import com.jj.templateproject.domain.theme.ThemeMode
import com.jj.templateproject.util.MainDispatcherExtension
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class MainRootViewModelTest {

    @JvmField
    @RegisterExtension
    val mainDispatcher = MainDispatcherExtension()

    private val getMainAdUnitId = mockk<GetMainAdUnitId>()
    private val getThemeModeUseCase = mockk<GetThemeModeUseCase>()

    private fun createViewModel(themeMode: ThemeMode = ThemeMode.SYSTEM): MainRootViewModel {
        every { getMainAdUnitId() } returns "ca-app-pub/main"
        every { getThemeModeUseCase() } returns flowOf(themeMode)
        return MainRootViewModel(getMainAdUnitId, getThemeModeUseCase)
    }

    @Test
    fun `initial state exposes the main ad unit id`() {
        assertEquals("ca-app-pub/main", createViewModel().viewState.value.adMainUnitId)
    }

    @Test
    fun `theme mode is exposed from the use case`() {
        assertEquals(ThemeMode.DARK, createViewModel(themeMode = ThemeMode.DARK).themeMode.value)
    }

    @Test
    fun `onAdClicked does not mutate state`() {
        val viewModel = createViewModel()
        val before = viewModel.viewState.value

        viewModel.onAdClicked()

        assertEquals(before, viewModel.viewState.value)
    }
}

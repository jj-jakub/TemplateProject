package com.jj.templateproject.domain.theme

import com.jj.templateproject.domain.preferences.AppPreferencesRepository
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ThemeModeUseCasesTest {

    private val repository = mockk<AppPreferencesRepository>(relaxed = true)

    @Test
    fun `get use case exposes the repository theme flow`() = runTest {
        every { repository.themeMode } returns flowOf(ThemeMode.DARK)

        val result = GetThemeModeUseCase(repository).invoke().first()

        assertEquals(ThemeMode.DARK, result)
    }

    @Test
    fun `set use case delegates to the repository`() = runTest {
        SetThemeModeUseCase(repository).invoke(ThemeMode.LIGHT)

        coVerify(exactly = 1) { repository.setThemeMode(ThemeMode.LIGHT) }
    }
}

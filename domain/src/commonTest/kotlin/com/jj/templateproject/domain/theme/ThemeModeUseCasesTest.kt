package com.jj.templateproject.domain.theme

import com.jj.templateproject.domain.preferences.FakeAppPreferencesRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ThemeModeUseCasesTest {

    @Test
    fun `get use case exposes the repository theme flow`() = runTest {
        val repository = FakeAppPreferencesRepository(themeMode = ThemeMode.DARK)

        val result = GetThemeModeUseCase(repository).invoke().first()

        assertEquals(ThemeMode.DARK, result)
    }

    @Test
    fun `set use case writes through to the repository`() = runTest {
        // Asserting the stored value rather than that a method was called: what matters is that
        // reading the preference back afterwards gives the new mode.
        val repository = FakeAppPreferencesRepository(themeMode = ThemeMode.SYSTEM)

        SetThemeModeUseCase(repository).invoke(ThemeMode.LIGHT)

        assertEquals(ThemeMode.LIGHT, repository.themeMode.first())
    }
}

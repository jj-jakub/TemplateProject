package com.jj.templateproject.presentation.ui.secondary

import androidx.lifecycle.SavedStateHandle
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SecondaryScreenViewModelTest {

    @Test
    fun `reads all three navigation arguments into state`() {
        val savedStateHandle = SavedStateHandle(
            mapOf(
                "text" to "primary",
                "textSecondary" to "secondary",
                "textTertiary" to "tertiary",
            )
        )

        val viewModel = SecondaryScreenViewModel(savedStateHandle)
        val state = viewModel.viewState.value

        assertEquals("primary", state.text)
        assertEquals("secondary", state.secondaryText)
        assertEquals("tertiary", state.tertiaryText)
    }

    @Test
    fun `missing arguments default to empty strings`() {
        val viewModel = SecondaryScreenViewModel(SavedStateHandle())
        val state = viewModel.viewState.value

        assertEquals("", state.text)
        assertEquals("", state.secondaryText)
        assertEquals("", state.tertiaryText)
    }

    @Test
    fun `only the provided arguments are populated`() {
        val savedStateHandle = SavedStateHandle(mapOf("text" to "only-primary"))

        val viewModel = SecondaryScreenViewModel(savedStateHandle)
        val state = viewModel.viewState.value

        assertEquals("only-primary", state.text)
        assertEquals("", state.secondaryText)
        assertEquals("", state.tertiaryText)
    }
}

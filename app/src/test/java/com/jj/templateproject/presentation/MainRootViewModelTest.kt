package com.jj.templateproject.presentation

import com.jj.templateproject.data.ad.GetMainAdUnitId
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MainRootViewModelTest {

    private val getMainAdUnitId = mockk<GetMainAdUnitId>()

    @Test
    fun `initial state exposes the main ad unit id`() {
        every { getMainAdUnitId() } returns "ca-app-pub/main"

        val viewModel = MainRootViewModel(getMainAdUnitId)

        assertEquals("ca-app-pub/main", viewModel.viewState.value.adMainUnitId)
    }

    @Test
    fun `onAdClicked does not mutate state`() {
        every { getMainAdUnitId() } returns "ca-app-pub/main"
        val viewModel = MainRootViewModel(getMainAdUnitId)
        val before = viewModel.viewState.value

        viewModel.onAdClicked()

        assertEquals(before, viewModel.viewState.value)
    }
}

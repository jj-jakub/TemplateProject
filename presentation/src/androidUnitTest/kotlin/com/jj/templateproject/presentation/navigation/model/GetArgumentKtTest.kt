package com.jj.templateproject.presentation.navigation.model

import androidx.lifecycle.SavedStateHandle
import io.mockk.every
import io.mockk.mockk
import kotlin.reflect.KProperty1
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/** MockK-based (mocking `SavedStateHandle`), so this stays androidUnitTest. */
class GetArgumentKtTest {

    private val savedStateHandle = mockk<SavedStateHandle>(relaxed = true)

    @Test
    fun `getArgument should return value when present`() {
        val property: KProperty1<Route.SecondaryScreen, String> = Route.SecondaryScreen::text
        val expectedValue = "testText"

        every { savedStateHandle.get<String>(property.name) } returns expectedValue

        val result = savedStateHandle.getArgument(property)

        assertEquals(expectedValue, result)
    }

    @Test
    fun `getArgument should return null when value is not present`() {
        val property: KProperty1<Route.SecondaryScreen, String> = Route.SecondaryScreen::text

        every { savedStateHandle.get<String>(property.name) } returns null

        val result = savedStateHandle.getArgument(property)

        assertNull(result)
    }
}

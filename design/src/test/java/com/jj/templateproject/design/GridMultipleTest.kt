package com.jj.templateproject.design

import androidx.compose.ui.unit.dp
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GridMultipleTest {

    @Test
    fun `whole multiples return 8dp per unit`() {
        assertEquals(8.dp, gridMultiple(1))
        assertEquals(16.dp, gridMultiple(2))
        assertEquals(80.dp, gridMultiple(10))
    }

    @Test
    fun `half multiples return 4dp per half unit`() {
        assertEquals(4.dp, gridMultiple(0.5))
        assertEquals(12.dp, gridMultiple(1.5))
    }

    @Test
    fun `zero and negative values return zero`() {
        assertEquals(0.dp, gridMultiple(0))
        assertEquals(0.dp, gridMultiple(-1))
        assertEquals(0.dp, gridMultiple(-0.5))
    }

    @Test
    fun `values that are not increments of 0_5 return zero`() {
        assertEquals(0.dp, gridMultiple(0.3))
        assertEquals(0.dp, gridMultiple(0.75))
    }
}

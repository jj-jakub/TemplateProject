package com.jj.templateproject.design

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

class ShapesTest {

    @Test
    fun `shape scale is wired to the 8dp grid`() {
        assertEquals(RoundedCornerShape(4.dp), Shapes.extraSmall)
        assertEquals(RoundedCornerShape(8.dp), Shapes.small)
        assertEquals(RoundedCornerShape(12.dp), Shapes.medium)
        assertEquals(RoundedCornerShape(16.dp), Shapes.large)
        assertEquals(RoundedCornerShape(28.dp), Shapes.extraLarge)
    }
}

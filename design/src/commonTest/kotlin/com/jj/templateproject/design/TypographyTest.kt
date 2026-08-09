package com.jj.templateproject.design

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlin.test.Test
import kotlin.test.assertEquals

class TypographyTest {

    @Test
    fun `type scale uses the M3 baseline sizes`() {
        assertEquals(57.sp, Typography.displayLarge.fontSize)
        assertEquals(36.sp, Typography.displaySmall.fontSize)
        assertEquals(32.sp, Typography.headlineLarge.fontSize)
        assertEquals(22.sp, Typography.titleLarge.fontSize)
        assertEquals(16.sp, Typography.bodyLarge.fontSize)
        assertEquals(14.sp, Typography.bodyMedium.fontSize)
        assertEquals(12.sp, Typography.bodySmall.fontSize)
        assertEquals(14.sp, Typography.labelLarge.fontSize)
        assertEquals(11.sp, Typography.labelSmall.fontSize)
    }

    // Kotlin/Native rejects a comma inside a backticked name, so this reads "while" rather than
    // the comma the JVM-only version used.
    @Test
    fun `title and label roles are medium weight while body roles are normal`() {
        assertEquals(FontWeight.Medium, Typography.titleMedium.fontWeight)
        assertEquals(FontWeight.Medium, Typography.labelLarge.fontWeight)
        assertEquals(FontWeight.Normal, Typography.bodyLarge.fontWeight)
        assertEquals(FontWeight.Normal, Typography.displayLarge.fontWeight)
    }

    @Test
    fun `every role uses the single brand font-family seam`() {
        val roles = listOf(
            Typography.displayLarge, Typography.displayMedium, Typography.displaySmall,
            Typography.headlineLarge, Typography.headlineMedium, Typography.headlineSmall,
            Typography.titleLarge, Typography.titleMedium, Typography.titleSmall,
            Typography.bodyLarge, Typography.bodyMedium, Typography.bodySmall,
            Typography.labelLarge, Typography.labelMedium, Typography.labelSmall,
        )
        roles.forEach { style ->
            assertEquals(AppFontFamily, style.fontFamily)
        }
    }

    @Test
    fun `line height is set for readability on every role`() {
        assertEquals(64.sp, Typography.displayLarge.lineHeight)
        assertEquals(24.sp, Typography.bodyLarge.lineHeight)
        assertEquals(16.sp, Typography.labelSmall.lineHeight)
    }
}

package com.jj.templateproject.design

import androidx.compose.ui.graphics.luminance
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class ColorSchemesTest {

    @Test
    fun `light scheme binds the light tokens`() {
        assertEquals(md_light_primary, LightColorScheme.primary)
        assertEquals(md_light_onPrimary, LightColorScheme.onPrimary)
        assertEquals(md_light_secondary, LightColorScheme.secondary)
        assertEquals(md_light_tertiary, LightColorScheme.tertiary)
        assertEquals(md_light_surfaceVariant, LightColorScheme.surfaceVariant)
        assertEquals(md_light_error, LightColorScheme.error)
        assertEquals(md_light_errorContainer, LightColorScheme.errorContainer)
        assertEquals(md_light_outline, LightColorScheme.outline)
        assertEquals(md_light_inversePrimary, LightColorScheme.inversePrimary)
    }

    @Test
    fun `dark scheme binds the dark tokens`() {
        assertEquals(md_dark_primary, DarkColorScheme.primary)
        assertEquals(md_dark_onPrimary, DarkColorScheme.onPrimary)
        assertEquals(md_dark_secondary, DarkColorScheme.secondary)
        assertEquals(md_dark_tertiary, DarkColorScheme.tertiary)
        assertEquals(md_dark_surfaceVariant, DarkColorScheme.surfaceVariant)
        assertEquals(md_dark_error, DarkColorScheme.error)
        assertEquals(md_dark_errorContainer, DarkColorScheme.errorContainer)
        assertEquals(md_dark_outline, DarkColorScheme.outline)
        assertEquals(md_dark_inversePrimary, DarkColorScheme.inversePrimary)
    }

    @Test
    fun `dark theme is genuinely distinct from light theme`() {
        assertNotEquals(LightColorScheme.background, DarkColorScheme.background)
        assertNotEquals(LightColorScheme.surface, DarkColorScheme.surface)
        // The dark surface/background must actually be dark, not a recolored light tint.
        assertTrue(
            DarkColorScheme.background.luminance() < LightColorScheme.background.luminance(),
            "dark background should be darker than light background",
        )
        assertTrue(
            DarkColorScheme.background.luminance() < 0.1f,
            "dark background should have low luminance",
        )
    }

    @Test
    fun `on-colors contrast with their containers`() {
        // A sign the palette is coherent: on* roles sit on the opposite luminance side of the
        // surface they label, in both themes.
        assertTrue(LightColorScheme.onBackground.luminance() < LightColorScheme.background.luminance())
        assertTrue(DarkColorScheme.onBackground.luminance() > DarkColorScheme.background.luminance())
        assertTrue(LightColorScheme.onPrimary.luminance() > LightColorScheme.primary.luminance())
    }

    @Test
    fun `legacy brand background aliases the light token`() {
        assertEquals(md_light_background, colorBackground)
    }
}

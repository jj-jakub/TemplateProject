package com.jj.templateproject.design

import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test

class DynamicColorTest {

    @Test
    fun `brand fallback returns the dark scheme in dark mode`() {
        assertSame(DarkColorScheme, brandColorScheme(darkTheme = true))
    }

    @Test
    fun `brand fallback returns the light scheme in light mode`() {
        assertSame(LightColorScheme, brandColorScheme(darkTheme = false))
    }
}

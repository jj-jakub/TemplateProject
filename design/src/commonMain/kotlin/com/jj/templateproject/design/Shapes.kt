package com.jj.templateproject.design

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes

/**
 * The Material 3 shape scale, replacing the previously empty `Shapes()`.
 *
 * Corner radii are derived from [gridMultiple] (the shared 8dp grid) so rounding stays in step
 * with spacing across the app and can be retuned in one place. Components reference these via
 * `MaterialTheme.shapes` (e.g. buttons use `small`, cards use `medium`) instead of hard-coding
 * a `RoundedCornerShape`.
 */
val Shapes = Shapes(
    extraSmall = RoundedCornerShape(gridMultiple(i = 0.5)), // 4dp
    small = RoundedCornerShape(gridMultiple(i = 1)), // 8dp
    medium = RoundedCornerShape(gridMultiple(i = 1.5)), // 12dp
    large = RoundedCornerShape(gridMultiple(i = 2)), // 16dp
    extraLarge = RoundedCornerShape(gridMultiple(i = 3.5)), // 28dp
)

package com.jj.templateproject.design

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Provides material grid multiples of 4 (0.5) and 8 (1). Only use increments of 0.5 and 1.
fun gridMultiple(i: Double): Dp {
    return when {
        i > 0 && i.mod(0.5) == 0.0 -> (8 * i).dp
        else -> 0.dp
    }
}

fun gridMultiple(i: Int): Dp = gridMultiple(i.toDouble())
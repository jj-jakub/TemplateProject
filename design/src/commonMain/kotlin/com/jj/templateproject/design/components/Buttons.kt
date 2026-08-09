package com.jj.templateproject.design.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/** Minimum touch target per the Material accessibility guidance. */
internal val MinTouchTargetSize = 48.dp

private val IconSize = 18.dp
private val IconSpacing = 8.dp

/**
 * The app's primary call-to-action button: filled, themed via `MaterialTheme.shapes.small`, with a
 * guaranteed 48dp minimum touch target and an optional leading icon.
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = MaterialTheme.shapes.small,
        modifier = modifier.heightIn(min = MinTouchTargetSize),
    ) {
        ButtonContent(text = text, leadingIcon = leadingIcon)
    }
}

/** A lower-emphasis outlined button, sharing [PrimaryButton]'s shape and touch-target rules. */
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        shape = MaterialTheme.shapes.small,
        modifier = modifier.heightIn(min = MinTouchTargetSize),
    ) {
        ButtonContent(text = text, leadingIcon = leadingIcon)
    }
}

@Composable
private fun ButtonContent(text: String, leadingIcon: ImageVector?) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(IconSpacing),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null, // decorative; the label conveys the action
                modifier = Modifier.size(IconSize),
            )
        }
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}

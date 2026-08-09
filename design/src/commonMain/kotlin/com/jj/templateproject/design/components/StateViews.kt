package com.jj.templateproject.design.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import com.jj.templateproject.design.TestTags
import com.jj.templateproject.design.gridMultiple

/**
 * Standardised async-state slots so every screen renders loading/error/empty the same accessible
 * way. Each carries a stable [TestTags] value for reliable UI assertions.
 */

/** Centered progress indicator with a screen-reader description. */
@Composable
fun LoadingState(
    modifier: Modifier = Modifier,
    description: String = "Loading",
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .testTag(TestTags.LOADING_STATE),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.semantics { contentDescription = description },
        )
    }
}

/** An error message with an optional Retry action (shown only when [onRetry] is provided). */
@Composable
fun ErrorState(
    message: String,
    modifier: Modifier = Modifier,
    retryLabel: String = "Retry",
    onRetry: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag(TestTags.ERROR_STATE)
            .padding(gridMultiple(i = 2)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(gridMultiple(i = 1)),
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
        )
        if (onRetry != null) {
            SecondaryButton(
                text = retryLabel,
                onClick = onRetry,
                modifier = Modifier.testTag(TestTags.ERROR_RETRY),
            )
        }
    }
}

/** An empty-content placeholder with a title and an optional call to action. */
@Composable
fun EmptyState(
    title: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag(TestTags.EMPTY_STATE)
            .padding(gridMultiple(i = 2)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(gridMultiple(i = 1)),
    ) {
        SectionHeader(text = title)
        if (actionLabel != null && onAction != null) {
            PrimaryButton(
                text = actionLabel,
                onClick = onAction,
                modifier = Modifier.testTag(TestTags.EMPTY_ACTION),
            )
        }
    }
}

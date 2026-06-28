package com.jj.templateproject.presentation.ui.state

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.jj.templateproject.R
import com.jj.templateproject.design.components.EmptyState
import com.jj.templateproject.design.components.ErrorState
import com.jj.templateproject.design.components.LoadingState

/**
 * Renders the design-system slot matching [state]: [LoadingState], [EmptyState], [ErrorState]
 * (with an optional [onRetry]) or the caller-provided [success] content. New screens get
 * consistent loading/error/empty handling for free by routing their state through this.
 *
 * The slot labels default to localized string resources, so error/empty/loading UI respects the
 * current locale; callers can still override them per screen.
 */
@Composable
fun <T> UiStateContent(
    state: UiState<T>,
    modifier: Modifier = Modifier,
    loadingDescription: String = stringResource(R.string.loading),
    retryLabel: String = stringResource(R.string.retry),
    emptyTitle: String = stringResource(R.string.empty_default),
    onRetry: (() -> Unit)? = null,
    success: @Composable (T) -> Unit,
) {
    when (state) {
        UiState.Loading -> LoadingState(modifier = modifier, description = loadingDescription)
        UiState.Empty -> EmptyState(title = emptyTitle, modifier = modifier)
        is UiState.Error -> ErrorState(
            message = state.message,
            modifier = modifier,
            retryLabel = retryLabel,
            onRetry = onRetry,
        )

        is UiState.Success -> success(state.data)
    }
}

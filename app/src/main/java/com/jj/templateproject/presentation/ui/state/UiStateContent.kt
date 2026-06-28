package com.jj.templateproject.presentation.ui.state

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.jj.templateproject.design.components.EmptyState
import com.jj.templateproject.design.components.ErrorState
import com.jj.templateproject.design.components.LoadingState

/**
 * Renders the design-system slot matching [state]: [LoadingState], [EmptyState], [ErrorState]
 * (with an optional [onRetry]) or the caller-provided [success] content. New screens get
 * consistent loading/error/empty handling for free by routing their state through this.
 */
@Composable
fun <T> UiStateContent(
    state: UiState<T>,
    modifier: Modifier = Modifier,
    emptyTitle: String = "Nothing here",
    onRetry: (() -> Unit)? = null,
    success: @Composable (T) -> Unit,
) {
    when (state) {
        UiState.Loading -> LoadingState(modifier = modifier)
        UiState.Empty -> EmptyState(title = emptyTitle, modifier = modifier)
        is UiState.Error -> ErrorState(message = state.message, modifier = modifier, onRetry = onRetry)
        is UiState.Success -> success(state.data)
    }
}

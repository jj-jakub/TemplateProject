package com.jj.templateproject.presentation.ui.secondary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.jj.templateproject.design.components.BodyText
import com.jj.templateproject.presentation.generated.resources.Res
import com.jj.templateproject.presentation.generated.resources.secondary_first
import com.jj.templateproject.presentation.generated.resources.secondary_secondary
import com.jj.templateproject.presentation.generated.resources.secondary_tertiary
import org.jetbrains.compose.resources.stringResource

@Composable
fun SecondaryScreen(
    viewModel: SecondaryScreenViewModel,
) {
    val state by viewModel.viewState.collectAsState()

    SecondaryScreenContent(
        text = state.text,
        secondaryText = state.secondaryText,
        tertiaryText = state.tertiaryText,
    )
}

// internal, not private: see MainScreen.kt's identical note on why the preview lives elsewhere.
@Composable
internal fun SecondaryScreenContent(
    text: String,
    secondaryText: String,
    tertiaryText: String,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BodyText(
            text = stringResource(Res.string.secondary_first, text),
        )
        BodyText(
            text = stringResource(Res.string.secondary_secondary, secondaryText),
        )
        BodyText(
            text = stringResource(Res.string.secondary_tertiary, tertiaryText),
        )
    }
}

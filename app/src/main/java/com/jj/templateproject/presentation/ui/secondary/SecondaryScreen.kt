package com.jj.templateproject.presentation.ui.secondary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.jj.templateproject.R
import com.jj.templateproject.design.ThemePreviews
import com.jj.templateproject.design.components.BodyText

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

@Composable
private fun SecondaryScreenContent(
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
            text = stringResource(R.string.secondary_first, text),
        )
        BodyText(
            text = stringResource(R.string.secondary_secondary, secondaryText),
        )
        BodyText(
            text = stringResource(R.string.secondary_tertiary, tertiaryText),
        )
    }
}

@ThemePreviews
@Composable
fun PreviewSecondaryScreen() {
    SecondaryScreenContent(
        text = "state.text",
        secondaryText = "state.secondaryText",
        tertiaryText = "state.tertiaryText",
    )
}


package com.jj.templateproject.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.jj.templateproject.design.TemplateTheme
import com.jj.templateproject.navigation.MainNavGraph
import com.jj.templateproject.presentation.ui.ads.ComposeAdView

@Composable
fun MainRoot(
    navController: NavHostController,
    viewModel: MainRootViewModel,
) {
    val state by viewModel.viewState.collectAsState()

    TemplateTheme {
        Box(modifier = Modifier.background(color = MaterialTheme.colorScheme.background)) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                ComposeAdView(
                    adUnitId = state.adMainUnitId,
                    onAdClicked = viewModel::onAdClicked,
                )
                MainNavGraph(
                    navController = navController,
                )
            }
        }
    }
}
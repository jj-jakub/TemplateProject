package com.jj.templateproject.presentation.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.jj.templateproject.design.TemplateTheme

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(
    viewModel: MainScreenViewModel,
) {
    val state by viewModel.viewState.collectAsState()
    val permissionState = rememberMultiplePermissionsState(
        state.requiredPermissions,
        onPermissionsResult = {
            // TODO
        },
    )
    LaunchedEffect(key1 = Unit) {
        permissionState.launchMultiplePermissionRequest()
    }

    MainScreenViewContent(
        loading = state.loading,
        text = state.text,
        status = state.status,
        data = state.data,
    )
}

@Composable
private fun MainScreenViewContent(
    loading: Boolean,
    text: String,
    status: String,
    data: String,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            modifier = Modifier.padding(
                bottom = com.jj.templateproject.design.gridMultiple(
                    i = 2
                )
            ),
            text = text,
        )
        Text(
            modifier = Modifier.padding(
                bottom = com.jj.templateproject.design.gridMultiple(
                    i = 2
                )
            ),
            text = status,
        )
        Text(
            modifier = Modifier.padding(
                bottom = com.jj.templateproject.design.gridMultiple(
                    i = 2
                )
            ),
            text = data,
        )
        CircularProgressIndicator(
            modifier = Modifier.alpha(if (loading) 1f else 0f)
        )
    }
}

@Preview
@Composable
fun PreviewMainScreenViewContent() {
    TemplateTheme {
        MainScreenViewContent(
            loading = true,
            data = "Data",
            status = "Status",
            text = "Sample text",
        )
    }
}


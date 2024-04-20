package com.jj.templateproject.design.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ErrorLabel(message: String) {
    Text(
        text = message,
        color = MaterialTheme.colorScheme.error
    )
}

@Preview
@Composable
fun PreviewErrorLabel() {
    ErrorLabel(message = "Login error: wrong username")
}
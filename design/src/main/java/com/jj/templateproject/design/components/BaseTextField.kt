package com.jj.templateproject.design.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun BaseTextField(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier,
        text = text,
    )
}

@Preview
@Composable
fun PreviewBaseTextField() {
    BaseTextField("Text")
}
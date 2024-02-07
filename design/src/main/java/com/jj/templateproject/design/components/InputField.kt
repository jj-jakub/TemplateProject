package com.jj.templateproject.design.components

import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun InputField(
    label: @Composable (() -> Unit)? = null,
    value: String,
    onValueChange: (String) -> Unit,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    TextField(
        label = label,
        value = value,
        onValueChange = onValueChange,
        visualTransformation = visualTransformation,
    )
}

@Preview
@Composable
fun PreviewInputField() {
    InputField(
        value = "",
        onValueChange = {},
        visualTransformation = PasswordVisualTransformation(),
    )
}

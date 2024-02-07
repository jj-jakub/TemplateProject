package com.jj.templateproject.presentation.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import com.jj.templateproject.design.TemplateTheme
import com.jj.templateproject.design.components.ActionButton
import com.jj.templateproject.design.components.InputField
import com.jj.templateproject.design.gridMultiple
import com.jj.templateproject.presentation.ui.login.model.LoginScreenNavigation

@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: LoginScreenViewModel,
) {
    val state by viewModel.viewState.collectAsState()

    LaunchedEffect(key1 = Unit) {
        viewModel.navigation.collect { navigation ->
            when (navigation) {
                is LoginScreenNavigation.MainScreen,
                LoginScreenNavigation.CreateAccountScreen,
                -> navController.navigate(route = navigation.route)
            }
        }
    }

    LoginScreenContent(
        isLoading = state.isLoading,
        usernameValue = state.username,
        passwordValue = state.password,
        onUsernameChanged = viewModel::onUsernameChanged,
        onPasswordChanged = viewModel::onPasswordChanged,
        onLoginClicked = viewModel::onLoginClicked,
        onSignInClicked = viewModel::onSignInClicked,
    )
}

@Composable
private fun LoginScreenContent(
    isLoading: Boolean,
    usernameValue: String,
    passwordValue: String,
    onUsernameChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onLoginClicked: () -> Unit,
    onSignInClicked: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier.width(IntrinsicSize.Max),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(gridMultiple(i = 4)),
        ) {
            if (isLoading) {
                CircularProgressIndicator()
                return
            }
            Text("Welcome")
            InputField(
                label = { Text("Username") },
                value = usernameValue,
                onValueChange = onUsernameChanged,
            )
            InputField(
                label = { Text("Password") },
                value = passwordValue,
                onValueChange = onPasswordChanged,
                visualTransformation = PasswordVisualTransformation(),
            )
            ActionButton(
                modifier = Modifier.fillMaxWidth(),
                text = "Log in",
                onClick = onLoginClicked,
            )
            ActionButton(
                modifier = Modifier.fillMaxWidth(),
                text = "Sign in",
                onClick = onSignInClicked,
            )
        }
    }
}

@Preview
@Composable
fun PreviewLoginScreen() {
    TemplateTheme {
        LoginScreenContent(
            isLoading = false,
            usernameValue = "",
            passwordValue = "",
            onUsernameChanged = {},
            onPasswordChanged = {},
            onLoginClicked = {},
            onSignInClicked = {},
        )
    }
}

@Preview
@Composable
fun PreviewLoginScreenLoading() {
    TemplateTheme {
        LoginScreenContent(
            isLoading = true,
            usernameValue = "",
            passwordValue = "",
            onUsernameChanged = {},
            onPasswordChanged = {},
            onLoginClicked = {},
            onSignInClicked = {},
        )
    }
}


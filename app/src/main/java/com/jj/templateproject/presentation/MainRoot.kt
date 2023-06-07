package com.jj.templateproject.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.jj.templateproject.design.TemplateTheme
import com.jj.templateproject.navigation.MainNavGraph

@Composable
fun MainRoot(navController: NavHostController) {
    TemplateTheme {
        Box(modifier = Modifier.background(color = MaterialTheme.colorScheme.background)) {
            MainNavGraph(
                navController = navController,
            )
        }
    }
}
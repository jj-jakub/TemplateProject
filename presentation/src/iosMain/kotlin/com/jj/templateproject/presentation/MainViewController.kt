package com.jj.templateproject.presentation

import androidx.compose.ui.window.ComposeUIViewController
import androidx.navigation.compose.rememberNavController
import org.koin.compose.viewmodel.koinViewModel
import platform.UIKit.UIViewController

/**
 * The one entry point Swift calls (`MainViewControllerKt.MainViewController()` from the generated
 * framework header, wrapped as a SwiftUI `UIViewControllerRepresentable` — see
 * `iosApp/iosApp/ContentView.swift`). Requires [initKoin] to have already run; `iOSApp.init()` is
 * where that happens, mirroring `TemplateProjectApplication.onCreate()` on Android.
 *
 * No push destination is threaded through here (`MainRoot`'s `pushDestination` param defaults to
 * null): notification/deep-link handling has no iOS counterpart yet, the same way `ComposeAdView`
 * and `AdManager` do not.
 */
// PascalCase is the Kotlin/Native convention for a factory function named after the type it
// vends (the same reason Compose's own `ComposeUIViewController` reads this way), so it reads
// naturally as `MainViewControllerKt.MainViewController()` from the Swift side.
@Suppress("FunctionNaming")
fun MainViewController(): UIViewController = ComposeUIViewController {
    MainRoot(
        navController = rememberNavController(),
        viewModel = koinViewModel(),
    )
}

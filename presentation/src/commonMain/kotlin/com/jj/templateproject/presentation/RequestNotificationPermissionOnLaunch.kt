package com.jj.templateproject.presentation

import androidx.compose.runtime.Composable

/**
 * Asks for the notification runtime permission once, when the composable enters composition.
 *
 * Both `MainScreen` and `SettingsScreen` used to carry this inline via
 * `accompanist-permissions`' `rememberMultiplePermissionsState`, each duplicating the same
 * `Build.VERSION.SDK_INT >= TIRAMISU` check and the same do-nothing result callback (a template
 * seam: react to the grant/denial here if a real app needs to). Pulled out once because it is
 * Android-only in a way neither screen's own content is — `accompanist-permissions` has no Compose
 * Multiplatform port, and "ask the OS for a runtime permission" is not a concept iOS shares at all
 * — which is what makes both screens otherwise fully shareable.
 *
 * The Android actual does the real ask; the iOS actual is a no-op, since there is no equivalent
 * permission to request there.
 */
@Composable
expect fun RequestNotificationPermissionOnLaunch()

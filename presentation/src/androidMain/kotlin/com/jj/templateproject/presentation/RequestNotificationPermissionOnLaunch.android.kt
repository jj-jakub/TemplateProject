package com.jj.templateproject.presentation

import android.Manifest
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
actual fun RequestNotificationPermissionOnLaunch() {
    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        listOf(Manifest.permission.POST_NOTIFICATIONS)
    } else {
        emptyList()
    }
    // Below 33 there is nothing to request, and rememberMultiplePermissionsState with an empty
    // list is harmless (it just reports every permission already granted), so no separate branch
    // is needed for that case.
    val permissionState = rememberMultiplePermissionsState(
        permissions,
        onPermissionsResult = { _: Map<String, Boolean> ->
            // Template seam: react to the grant/denial result here, e.g. forward it to a
            // ViewModel to update UI state or show a rationale. Intentionally a no-op.
        },
    )
    LaunchedEffect(key1 = Unit) {
        permissionState.launchMultiplePermissionRequest()
    }
}

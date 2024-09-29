package com.prilepskiy.presentation.map.uiComponents

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationComponent(granted: () -> Unit, error: () -> Unit) {
    val localContext = LocalContext.current

    val permissionState = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    ) { isGranted ->
        localContext.run {
            if (isGranted) {
                granted.invoke()
            } else {
                error.invoke()
            }
        }
    }

    LaunchedEffect(key1 = Unit) {
        if (permissionState.status.isGranted) {
            granted.invoke()
        } else {
            permissionState.launchPermissionRequest()
        }
    }
}

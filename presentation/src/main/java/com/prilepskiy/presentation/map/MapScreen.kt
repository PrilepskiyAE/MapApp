package com.prilepskiy.presentation.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay

@Composable
fun MapScreen(viewModel: MapViewModel = hiltViewModel()) {
    val state = viewModel.viewState
    LaunchedEffect(Unit) {
        viewModel.onIntent(MapIntent.OnLoading(isLoading = true))
        delay(10000)
        viewModel.onIntent(MapIntent.OnLoading(isLoading = false))
    }
    Box(modifier = Modifier.fillMaxSize()) {
    if (state.isLoading) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center),
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    } else {
            Text(modifier = Modifier.align(Alignment.Center), text = "Route map")
        }
    }
}
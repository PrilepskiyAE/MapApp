package com.prilepskiy.presentation.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment

@Composable
fun MapScreen(){
    Box(modifier = Modifier.fillMaxSize()) {
        Text(modifier = Modifier.align(Alignment.Center), text = "Route map")
    }

}
package com.prilepskiy.mapapp.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.prilepskiy.mapapp.navigation.mapRoute
import com.prilepskiy.mapapp.navigation.mapScreen

@Composable
fun RootScreen(
    modifier: Modifier = Modifier,
    startDestination: String = mapRoute,
) {
    val rootNavController: NavHostController = rememberNavController()
    NavHost(
        navController = rootNavController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        mapScreen()
    }
}

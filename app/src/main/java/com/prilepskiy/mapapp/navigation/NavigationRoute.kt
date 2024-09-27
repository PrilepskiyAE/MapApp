package com.prilepskiy.mapapp.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.prilepskiy.presentation.map.MapScreen

const val mapRoute = "map_route"
fun NavGraphBuilder.mapScreen(){
    composable(route = mapRoute) {
        MapScreen()
    }
}

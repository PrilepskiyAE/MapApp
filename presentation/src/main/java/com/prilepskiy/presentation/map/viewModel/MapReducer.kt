package com.prilepskiy.presentation.map.viewModel

import com.prilepskiy.mvi.Reducer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MapReducer @Inject constructor() : Reducer<MapAction, MapState> {
    override fun reduce(action: MapAction, state: MapState): MapState = when (action) {
        is MapAction.OnError -> state.copy(
            isLoading = false,
            error = action.error
        )

        is MapAction.OnLoading -> state.copy(isLoading = action.isLoading)
        is MapAction.FetchCurrentLocation -> {
            if (state.currentGeolocation == null) {
                state.copy(currentGeolocation = action.geolocationModel, initLocation = true)
            } else {
                state.copy(currentGeolocation = action.geolocationModel)
            }
        }
        is MapAction.CreateTempPoints -> {
            val temp = state.tempPoints.toMutableList()
            temp.add(action.point)
            state.copy(tempPoints = temp)
        }
        is MapAction.OnCreateDialog -> state.copy(isCreateDialog = action.point)
        is MapAction.InitDbPoint -> state.copy(tempPoints = action.list)
        is MapAction.OnInfoDialog -> state.copy(isInfoDialog = action.point)
        is MapAction.OnDeletePoint -> {
            val temp = state.tempPoints.toMutableList()
            temp.remove(action.point)
            state.copy(tempPoints = temp)
        }
    }
}

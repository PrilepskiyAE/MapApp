package com.prilepskiy.presentation.map

import com.prilepskiy.mvi.Reducer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MapReducer@Inject constructor() : Reducer<MapAction, MapState> {
    override fun reduce(action: MapAction, state: MapState): MapState =  when (action) {
        is MapAction.OnError -> state.copy(
            isLoading = false,
            error = action.error
        )
        is MapAction.OnLoading -> state.copy(isLoading = action.isLoading)
    }
}

package com.prilepskiy.presentation.map

import com.prilepskiy.mvi.MviBaseViewModel
import com.prilepskiy.mvi.Reducer
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(private val  mapReducer: MapReducer) : MviBaseViewModel<MapState, MapAction, MapIntent>() {

    override var reducer: Reducer<MapAction, MapState> = mapReducer
    override fun initState(): MapState = MapState()

    override fun handleIntent(intent: MapIntent) {
        when(intent){
            is MapIntent.OnError -> onAction(MapAction.OnError(intent.error))

            is MapIntent.OnLoading -> onAction(MapAction.OnLoading(intent.isLoading))
        }
    }

}
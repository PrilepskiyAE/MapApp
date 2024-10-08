package com.prilepskiy.presentation.map.viewModel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.prilepskiy.common.DEFAULT_LOCATION
import com.prilepskiy.domain.location.GetLocationUseCase
import com.prilepskiy.domain.model.PointModel
import com.prilepskiy.domain.point.AddPointUseCase
import com.prilepskiy.domain.point.DeletePointUseCase
import com.prilepskiy.domain.point.GetAllPointUseCase
import com.prilepskiy.domain.point.GetPointUseCase
import com.prilepskiy.mvi.MviBaseViewModel
import com.prilepskiy.mvi.Reducer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val mapReducer: MapReducer,
    private val getLocationUseCase: GetLocationUseCase,
    private val addPointUseCase: AddPointUseCase,
    private val getAllPointUseCase: GetAllPointUseCase,
    private val getPointUseCase: GetPointUseCase,
    private val deletePointUseCase: DeletePointUseCase
) : MviBaseViewModel<MapState, MapAction, MapIntent>() {

    override var reducer: Reducer<MapAction, MapState> = mapReducer
    override fun initState(): MapState = MapState()

    init {
        viewModelScope.launch {
            onAction(MapAction.InitDbPoint(getAllPointUseCase.invoke()))
        }

    }

    override fun handleIntent(intent: MapIntent) {
        when (intent) {
            is MapIntent.OnError -> onAction(MapAction.OnError(intent.error))
            is MapIntent.OnLoading -> onAction(MapAction.OnLoading(intent.isLoading))
            is MapIntent.FetchCurrentLocation -> fetchLocation()
            is MapIntent.CreateTempPoints -> {
                viewModelScope.launch {
                    addPointUseCase(intent.point)
                    onAction(MapAction.CreateTempPoints(intent.point))
                }
            }
            is MapIntent.OnCreateDialog -> onAction(MapAction.OnCreateDialog(intent.point))
            is MapIntent.CreateDbPoints -> {
                viewModelScope.launch {
                    onAction(MapAction.CreateTempPoints(intent.point))
                }
            }

            is MapIntent.OnInfoDialog -> {
                viewModelScope.launch {
                    onAction(MapAction.OnInfoDialog(intent.point))
                }
            }

            is MapIntent.OnDeletePoint -> {
                viewModelScope.launch {
                    deletePointUseCase.invoke(
                        intent.point.latitude,
                        intent.point.longitude
                    )
                    onAction(MapAction.OnDeletePoint(intent.point))
                }
            }
        }
    }

    private fun fetchLocation() {
        viewModelScope.launch(Dispatchers.IO) {
            getLocationUseCase.invoke().collect { data ->
                data.fold(
                    ifLeft = {
                        onAction(MapAction.OnError(it.toString()))
                    },
                    ifRight = {
                        onAction(MapAction.FetchCurrentLocation(it))
                    }
                )
            }
        }
    }

    companion object {
        const val TAG = "MapViewModel"
    }
}

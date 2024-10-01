package com.prilepskiy.presentation.map.viewModel

import com.prilepskiy.common.EMPTY_STRING
import com.prilepskiy.domain.model.GeolocationModel
import com.prilepskiy.domain.model.PointModel
import com.prilepskiy.mvi.MviAction
import com.prilepskiy.mvi.MviIntent
import com.prilepskiy.mvi.MviState
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.PlacemarkMapObject

sealed class MapIntent : MviIntent {
    data class OnError(val error: String?) : MapIntent()
    data class OnLoading(val isLoading: Boolean) : MapIntent()
    data object FetchCurrentLocation : MapIntent()
    data class CreateTempPoints(val point: PointModel) :
        MapIntent()

    data class CreateDbPoints(val title: String, val point: PointModel) :
        MapIntent()


    data class OnCreateDialog(val point: Pair<Boolean, PointModel>) : MapIntent()
    data class OnInfoDialog(val point: Pair<Boolean, PointModel>) : MapIntent()
    data class OnDeletePoint(val point: PointModel) : MapIntent()
}

sealed class MapAction : MviAction {
    data class FetchCurrentLocation(val geolocationModel: GeolocationModel) : MapAction()
    data class OnError(val error: String?) : MapAction()
    data class OnLoading(val isLoading: Boolean) : MapAction()
    data class CreateTempPoints(val point: PointModel) : MapAction()
    data class OnCreateDialog(val point: Pair<Boolean, PointModel>) : MapAction()
    data class InitDbPoint(val list: List<PointModel>) : MapAction()
    data class OnInfoDialog(val point: Pair<Boolean,  PointModel>) : MapAction()
    data class OnDeletePoint(val point: PointModel) : MapAction()
}

data class MapState(
    val currentGeolocation: GeolocationModel? = null,
    val tempPoints: List<PointModel> = listOf(),
    val initLocation: Boolean = false,
    val isCreateDialog: Pair<Boolean, PointModel> = Pair(false, PointModel()),
    val isInfoDialog: Pair<Boolean, PointModel> = Pair(
        false,
        PointModel()
    ),
    override val error: String? = null,
    override val isLoading: Boolean = false,
) : MviState

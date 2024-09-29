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
    data class CreateTempPoints(val title: String, val placemarkMapObject: PlacemarkMapObject) :
        MapIntent()

    data class CreateDbPoints(val title: String, val placemarkMapObject: PlacemarkMapObject) :
        MapIntent()


    data class OnCreateDialog(val point: Pair<Boolean, Point>) : MapIntent()
    data class OnInfoDialog(val point: Triple<Boolean, String, PlacemarkMapObject?>) : MapIntent()
    data class OnDeletePoint(val point: PlacemarkMapObject) : MapIntent()
}

sealed class MapAction : MviAction {
    data class FetchCurrentLocation(val geolocationModel: GeolocationModel) : MapAction()
    data class OnError(val error: String?) : MapAction()
    data class OnLoading(val isLoading: Boolean) : MapAction()
    data class CreateTempPoints(val placemarkMapObject: PlacemarkMapObject) : MapAction()
    data class OnCreateDialog(val point: Pair<Boolean, Point>) : MapAction()
    data class InitDbPoint(val list: List<PointModel>) : MapAction()
    data class OnInfoDialog(val point: Triple<Boolean, String, PlacemarkMapObject?>) : MapAction()
    data class OnDeletePoint(val point: PlacemarkMapObject) : MapAction()
}

data class MapState(
    val currentGeolocation: GeolocationModel? = null,
    val tempPoints: Set<PlacemarkMapObject> = setOf(),
    val dbPoints: List<PointModel> = listOf(),
    val initLocation: Boolean = false,
    val isCreateDialog: Pair<Boolean, Point> = Pair(false, Point()),
    val isInfoDialog: Triple<Boolean, String, PlacemarkMapObject?> = Triple(
        false,
        EMPTY_STRING,
        null
    ),
    override val error: String? = null,
    override val isLoading: Boolean = false,
) : MviState

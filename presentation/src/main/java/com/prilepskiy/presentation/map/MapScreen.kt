package com.prilepskiy.presentation.map

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import com.prilepskiy.common.AZIMUTH
import com.prilepskiy.common.DEFAULT_LOCATION
import com.prilepskiy.common.TILT
import com.prilepskiy.common.ZOOM_STEP
import com.prilepskiy.domain.model.PointModel
import com.prilepskiy.presentation.R
import com.prilepskiy.presentation.map.uiComponents.AlertDialogAddPoint
import com.prilepskiy.presentation.map.uiComponents.AlertDialogError
import com.prilepskiy.presentation.map.uiComponents.AlertDialogInfoPoint
import com.prilepskiy.presentation.map.uiComponents.ComposableLifecycle
import com.prilepskiy.presentation.map.uiComponents.LocationComponent
import com.prilepskiy.presentation.map.uiComponents.MapInteractionButtons
import com.prilepskiy.presentation.map.viewModel.MapIntent
import com.prilepskiy.presentation.map.viewModel.MapState
import com.prilepskiy.presentation.map.viewModel.MapViewModel
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.DrivingOptions
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.directions.driving.DrivingRouterType
import com.yandex.mapkit.directions.driving.DrivingSession
import com.yandex.mapkit.directions.driving.VehicleOptions
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.mapview.MapView


@Composable
fun MapScreen(viewModel: MapViewModel = hiltViewModel()) {
    val state = viewModel.viewState
    val context = LocalContext.current
    val mapView by remember { mutableStateOf(MapView(context)) }
    val myGeoObject: MutableState<PlacemarkMapObject> = remember {
        mutableStateOf(
            mapView.trackMyPosition(
                context = context,
            )
        )
    }
    val placeMarkMapCollection: MutableState<List<Pair<PlacemarkMapObject, PointModel>>> =
        remember { mutableStateOf(listOf()) }
    val blockUpdate: MutableState<Boolean> = remember {
        mutableStateOf(false)
    }
    val onTapLisener: MutableState<InputListener> = remember {
        mutableStateOf(
            object : InputListener {
                override fun onMapTap(p0: Map, p1: Point) { /* no-op */
                }

                override fun onMapLongTap(p0: Map, p1: Point) {
                    viewModel.onIntent(
                        MapIntent.OnCreateDialog(
                            Pair(
                                true,
                                PointModel(latitude = p1.latitude, longitude = p1.longitude)
                            )
                        )
                    )
                }
            }
        )
    }
    var points by remember {
        mutableStateOf(buildList {
            add(
                RequestPoint(
                    Point(DEFAULT_LOCATION, DEFAULT_LOCATION),
                    RequestPointType.WAYPOINT,
                    null,
                    null
                )
            )
            add(
                RequestPoint(
                    Point(DEFAULT_LOCATION, DEFAULT_LOCATION),
                    RequestPointType.WAYPOINT,
                    null,
                    null
                )
            )
        })
    }
    var drivingSession: DrivingSession? by remember {
        mutableStateOf(null)
    }
    val drivingRouteListener = remember {
        object : DrivingSession.DrivingRouteListener {
            override fun onDrivingRoutes(drivingRoutes: MutableList<DrivingRoute>) {
                for (route in drivingRoutes) {
                    mapView.mapWindow.map.mapObjects.addPolyline(route.geometry)
                }
            }

            override fun onDrivingRoutesError(p0: com.yandex.runtime.Error) {
                /* no-op */
            }

        }
    }

    LocationComponent({ viewModel.onIntent(MapIntent.FetchCurrentLocation) }, {
        viewModel.onIntent(MapIntent.OnError(context.getString(R.string.permission_not_granted)))
    })

    LaunchedEffect(key1 = state.tempPoints) {
        refreshPoints(
            state,
            mapView,
            placeMarkMapCollection,
            myGeoObject,
            blockUpdate,
            context,
            viewModel
        )
    }
    LaunchedEffect(key1 = points) {
        refreshPoints(
            state,
            mapView,
            placeMarkMapCollection,
            myGeoObject,
            blockUpdate,
            context,
            viewModel
        )
        val drivingRouter =
            DirectionsFactory.getInstance().createDrivingRouter(DrivingRouterType.COMBINED)
        val drivingOptions = DrivingOptions().apply {
            routesCount = 1
        }

        val vehicleOptions = VehicleOptions()
        drivingSession = drivingRouter.requestRoutes(
            points,
            drivingOptions,
            vehicleOptions,
            drivingRouteListener
        )

    }
    if (state.isCreateDialog.first) {
        AlertDialogAddPoint(point = state.isCreateDialog.second, onNegative = {
            viewModel.onIntent(MapIntent.OnCreateDialog(Pair(false, PointModel())))
        }, onPositive = { point ->
            viewModel.onIntent(MapIntent.CreateTempPoints(point))
            viewModel.onIntent(MapIntent.OnCreateDialog(Pair(false, PointModel())))
        })
    }
    ComposableLifecycle { _, event ->
        when (event) {
            Lifecycle.Event.ON_CREATE -> {
                MapKitFactory.initialize(context)
            }

            Lifecycle.Event.ON_START -> {
                MapKitFactory.getInstance().onStart()
                mapView.onStart()

            }

            Lifecycle.Event.ON_RESUME -> {
                refreshPoints(
                    state,
                    mapView,
                    placeMarkMapCollection,
                    myGeoObject,
                    blockUpdate,
                    context,
                    viewModel
                )
            }

            Lifecycle.Event.ON_STOP -> {
                mapView.onStop()
                MapKitFactory.getInstance().onStop()
            }

            else -> {
                /* no-op */
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (state.error != null) {
            AlertDialogError(
                header = state.error,
                onAction = {
                    viewModel.onIntent(MapIntent.OnError(null))
                })
        }

        if (state.isInfoDialog.first) {
            AlertDialogInfoPoint(
                onPositive = {

                    points = buildList {
                        add(
                            RequestPoint(
                                myGeoObject.value.geometry,
                                RequestPointType.WAYPOINT,
                                null,
                                null
                            )
                        )
                        add(
                            RequestPoint(
                                Point(it.latitude, it.longitude),
                                RequestPointType.WAYPOINT,
                                null,
                                null
                            )
                        )
                    }
                    viewModel.onIntent(
                        MapIntent.OnInfoDialog(
                            Pair(
                                false,
                                PointModel()
                            )
                        )
                    )
                },
                onDelete = {
                    viewModel.onIntent(MapIntent.OnDeletePoint(it))
                    viewModel.onIntent(
                        MapIntent.OnInfoDialog(
                            Pair(
                                false,
                                PointModel()
                            )
                        )
                    )
                },
                onNegative = {
                    viewModel.onIntent(
                        MapIntent.OnInfoDialog(
                            Pair(
                                false,
                                PointModel()
                            )
                        )
                    )
                },
                point = state.isInfoDialog.second,
            )
        }


        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
            )
        } else {
            AndroidView(
                factory = {
                    mapView.apply {
                        mapWindow.map.addInputListener(onTapLisener.value)
                        mapWindow.map.move(
                            CameraPosition(
                                Point(
                                    state.currentGeolocation?.latitude ?: DEFAULT_LOCATION,
                                    state.currentGeolocation?.longitude ?: DEFAULT_LOCATION,
                                ), 25.0f, AZIMUTH, TILT
                            )
                        )
                    }
                },
                update = { map ->
                    map.mapWindow.map.addInputListener(onTapLisener.value)
                    if (!blockUpdate.value) {
                        if (state.currentGeolocation != null) {
                            myGeoObject.value.geometry = Point(
                                state.currentGeolocation.latitude,
                                state.currentGeolocation.longitude
                            )
                        }
                        placeMarkMapCollection.value.forEach { point ->
                            point.first.redraw(context, point.second)
                            point.first.addTapListener(placemarkTapListener(point.first) {
                                viewModel.onIntent(
                                    MapIntent.OnInfoDialog(
                                        Pair(
                                            true,
                                            point.second
                                        )
                                    )
                                )
                            })
                        }


                    }
                })


            MapInteractionButtons(
                modifier = Modifier
                    .align(Alignment.TopEnd),
                enabledLocation = state.initLocation,
                zoomPlusClicked = {
                    mapView.changeZoomByStep(ZOOM_STEP)
                },
                zoomMinusClicked = {
                    mapView.changeZoomByStep(-ZOOM_STEP)
                },
                myLocationClicked = {
                    if (state.initLocation) {
                        mapView.mapWindow.map.move(
                            CameraPosition(
                                Point(
                                    state.currentGeolocation?.latitude ?: DEFAULT_LOCATION,
                                    state.currentGeolocation?.longitude ?: DEFAULT_LOCATION,
                                ), 25.0f, AZIMUTH, TILT
                            )
                        )
                    }
                }
            )
        }
    }
}


private fun refreshPoints(
    state: MapState,
    mapView: MapView,
    placeMarkMapCollection: MutableState<List<Pair<PlacemarkMapObject, PointModel>>>,
    myGeoObject: MutableState<PlacemarkMapObject>,
    blockUpdate: MutableState<Boolean>,
    context: Context,
    viewModel: MapViewModel
) {
    val result: MutableList<Pair<PlacemarkMapObject, PointModel>> = mutableListOf()
    blockUpdate.value = true
    mapView.mapWindow.map.mapObjects.clear()
    mapView.mapWindow.map.addInputListener(object : InputListener {
        override fun onMapTap(p0: Map, p1: Point) {}
        override fun onMapLongTap(p0: Map, p1: Point) {
            viewModel.onIntent(
                MapIntent.OnCreateDialog(
                    Pair(
                        true,
                        PointModel(latitude = p1.latitude, longitude = p1.longitude)
                    )
                )
            )
        }
    })
    state.tempPoints.forEach {
        result.add(Pair(mapView.createObject(it), it))
    }
    placeMarkMapCollection.value = result
    myGeoObject.value = mapView.trackMyPosition(context = context)
    blockUpdate.value = false
}

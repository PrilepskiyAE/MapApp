package com.prilepskiy.presentation.map

import android.util.Log
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
import com.prilepskiy.common.EMPTY_STRING
import com.prilepskiy.common.TILT
import com.prilepskiy.common.ZOOM_STEP
import com.prilepskiy.presentation.R
import com.prilepskiy.presentation.map.uiComponents.AlertDialogAddPoint
import com.prilepskiy.presentation.map.uiComponents.AlertDialogError
import com.prilepskiy.presentation.map.uiComponents.AlertDialogInfoPoint
import com.prilepskiy.presentation.map.uiComponents.ComposableLifecycle
import com.prilepskiy.presentation.map.uiComponents.LocationComponent
import com.prilepskiy.presentation.map.uiComponents.MapInteractionButtons
import com.prilepskiy.presentation.map.viewModel.MapIntent
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
import com.yandex.runtime.Error


@Composable
fun MapScreen(viewModel: MapViewModel = hiltViewModel()) {
    val placeMarkMapCollection: MutableState<List<PlacemarkMapObject>> =
        remember { mutableStateOf(listOf()) }
    val state = viewModel.viewState
    val context = LocalContext.current
    val mapView by remember { mutableStateOf(MapView(context)) }
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
    var drivingSession:DrivingSession? by remember {
        mutableStateOf(null)
    }
    LocationComponent({
        viewModel.onIntent(MapIntent.FetchCurrentLocation)
    }, {
        viewModel.onIntent(MapIntent.OnError(context.getString(R.string.permission_not_granted)))
    })
    val myGeoObject: MutableState<PlacemarkMapObject> = remember {
        mutableStateOf(
            mapView.trackMyPosition(
                context = context,
            )
        )
    }
    val drivingRouteListener = remember {
        object : DrivingSession.DrivingRouteListener {
            override fun onDrivingRoutes(drivingRoutes: MutableList<DrivingRoute>) {
                for (route in drivingRoutes) {
                    mapView.mapWindow.map.mapObjects.addPolyline(route.geometry)
                }
            }

            override fun onDrivingRoutesError(error: Error) {}
        }
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
            Lifecycle.Event.ON_RESUME->{
                mapView.mapWindow.map.addInputListener(object : InputListener {
                    override fun onMapTap(p0: Map, p1: Point) {}

                    override fun onMapLongTap(p0: Map, p1: Point) {
                        viewModel.onIntent(MapIntent.OnCreateDialog(Pair(true, p1)))
                    }

                })
                placeMarkMapCollection.value = state.tempPoints.toList()

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
    LaunchedEffect(state.initLocation) {
        mapView.apply {
            mapView.mapWindow.map.move(
                CameraPosition(
                    Point(
                        state.currentGeolocation?.latitude ?: DEFAULT_LOCATION,
                        state.currentGeolocation?.longitude ?: DEFAULT_LOCATION
                    ), 20.0f, AZIMUTH, TILT
                )
            )
        }
    }
    LaunchedEffect(key1 = state.dbPoints) {
        state.dbPoints.forEach { model ->
            viewModel.onIntent(
                MapIntent.CreateDbPoints(
                    model.title,
                    mapView.createObject(Point(model.latitude, model.longitude))
                )
            )
        }
    }
    LaunchedEffect(key1 = state.tempPoints) {
        placeMarkMapCollection.value = state.tempPoints.toList()
        addListener(placeMarkMapCollection, viewModel)
    }
    LaunchedEffect(key1 = points) {
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
    Box(modifier = Modifier.fillMaxSize()) {
        if (state.error != null) {
            AlertDialogError(
                header = state.error,
                onAction = {
                    viewModel.onIntent(MapIntent.OnError(null))
                })
        }
        if (state.isCreateDialog.first) {
            AlertDialogAddPoint(point = state.isCreateDialog.second, onNegative = {
                viewModel.onIntent(MapIntent.OnCreateDialog(Pair(false, Point())))
            }, onPositive = { point, title ->
                viewModel.onIntent(MapIntent.CreateTempPoints(title, mapView.createObject(point)))
                viewModel.onIntent(MapIntent.OnCreateDialog(Pair(false, Point())))
            })
        }
        if (state.isInfoDialog.first) {
            state.isInfoDialog.third?.let { objectMap ->
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
                            add(RequestPoint(it.geometry, RequestPointType.WAYPOINT, null, null))
                        }

                        viewModel.onIntent(
                            MapIntent.OnInfoDialog(
                                Triple(
                                    false,
                                    EMPTY_STRING,
                                    null
                                )
                            )
                        )
                    },
                    onDelete = {
                        it.isVisible = false
                        val temp = placeMarkMapCollection.value.toMutableList()
                        temp.remove(it)
                        placeMarkMapCollection.value = temp
                        viewModel.onIntent(MapIntent.OnDeletePoint(it))
                        if (it.isValid) {
                            mapView.mapWindow.map.mapObjects.remove(it)
                        }
                        viewModel.onIntent(
                            MapIntent.OnInfoDialog(
                                Triple(
                                    false,
                                    EMPTY_STRING,
                                    null
                                )
                            )
                        )

                    },
                    onNegative = {
                        viewModel.onIntent(
                            MapIntent.OnInfoDialog(
                                Triple(
                                    false,
                                    EMPTY_STRING,
                                    myGeoObject.value
                                )
                            )
                        )
                    },
                    point = objectMap,
                    title = state.isInfoDialog.second,
                )
            }
        }
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
            )
        } else {
            AndroidView(
                modifier = Modifier,
                factory = {
                    mapView.apply {
                        mapWindow.map.addInputListener(object : InputListener {
                            override fun onMapTap(p0: Map, p1: Point) {}

                            override fun onMapLongTap(p0: Map, p1: Point) {
                                viewModel.onIntent(MapIntent.OnCreateDialog(Pair(true, p1)))
                            }

                        })
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
                    if (state.currentGeolocation != null) {
                        myGeoObject.value.geometry = Point(
                            state.currentGeolocation.latitude,
                            state.currentGeolocation.longitude
                        )
                        myGeoObject.value.isVisible = true
                    } else {
                        myGeoObject.value.isVisible = false
                    }
                    map.mapWindow.map.addInputListener(object : InputListener {
                        override fun onMapTap(p0: Map, p1: Point) {}

                        override fun onMapLongTap(p0: Map, p1: Point) {
                            viewModel.onIntent(MapIntent.OnCreateDialog(Pair(true, p1)))
                        }

                    })
                    placeMarkMapCollection.value.forEach {
                        it.redraw(context, it.geometry)
                    }
                    addListener(placeMarkMapCollection, viewModel)
                }
            )
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

private fun addListener(
    placeMarkMapCollection: MutableState<List<PlacemarkMapObject>>,
    viewModel: MapViewModel
) {
    placeMarkMapCollection.value.forEach {
        it.addTapListener(placemarkTapListener(it) {
            viewModel.onIntent(
                MapIntent.OnInfoDialog(
                    Triple(
                        true,
                        EMPTY_STRING,
                        it
                    )
                )
            )
        })
    }
}

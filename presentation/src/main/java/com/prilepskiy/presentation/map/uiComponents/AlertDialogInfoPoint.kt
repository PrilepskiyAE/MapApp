package com.prilepskiy.presentation.map.uiComponents

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.prilepskiy.common.EMPTY_STRING
import com.prilepskiy.common.ZOOM_SEARCH
import com.prilepskiy.domain.model.PointModel
import com.prilepskiy.presentation.R
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.search.Address
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManagerType
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.Session
import com.yandex.mapkit.search.ToponymObjectMetadata


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertDialogInfoPoint(
    onPositive: (point: PointModel) -> Unit,
    onDelete: (point: PointModel) -> Unit,
    onNegative: () -> Unit,
    point: PointModel,
    dismissOnClickOutside: Boolean = true,
    dismissOnBackPress: Boolean = true,
) {
    Dialog(
        onDismissRequest = onNegative,
        properties = DialogProperties(
            dismissOnClickOutside = dismissOnClickOutside,
            dismissOnBackPress = dismissOnBackPress,
            usePlatformDefaultWidth = false
        ),
    ) {
        val street = remember {
            mutableStateOf(EMPTY_STRING)
        }
        val house = remember {
            mutableStateOf(EMPTY_STRING)
        }
        val region = remember {
            mutableStateOf(EMPTY_STRING)
        }
        val searchListener = remember {
            object : Session.SearchListener {
                override fun onSearchResponse(response: Response) {
                    val collectionInfo = response.collection.children.firstOrNull()?.obj
                        ?.metadataContainer
                        ?.getItem(ToponymObjectMetadata::class.java)
                        ?.address
                        ?.components
                    street.value =
                        collectionInfo?.firstOrNull { it.kinds.contains(Address.Component.Kind.STREET) }?.name
                            ?: EMPTY_STRING
                    house.value =
                        collectionInfo?.firstOrNull { it.kinds.contains(Address.Component.Kind.HOUSE) }?.name
                            ?: EMPTY_STRING
                    region.value =
                        collectionInfo?.firstOrNull { it.kinds.contains(Address.Component.Kind.REGION) }?.name
                            ?: EMPTY_STRING

                }

                override fun onSearchError(p0: com.yandex.runtime.Error) {

                }

            }
        }
        val searchManager =
            SearchFactory.getInstance().createSearchManager(SearchManagerType.ONLINE)

        LaunchedEffect(key1 = Unit) {
            searchManager.submit(Point(point.latitude,point.longitude), ZOOM_SEARCH, SearchOptions(), searchListener)
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onNegative)

        ) {
            Card(onClick = {}) {
                Column(
                    modifier = Modifier
                        .width(280.dp)
                        .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 8.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = point.title)
                    Text(text = "${street.value} ${house.value} ${region.value}")
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onPositive.invoke(point) },
                    ) {
                        Text(text = stringResource(id = R.string.label_point))
                    }
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onDelete.invoke(point) },
                    ) {
                        Text(text = stringResource(id = R.string.delete_point))
                    }
                    Button(modifier = Modifier.fillMaxWidth(), onClick = { onNegative.invoke() }) {
                        Text(text = stringResource(id = R.string.button_negative))
                    }
                }
            }
        }
    }
}

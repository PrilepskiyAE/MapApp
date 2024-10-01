package com.prilepskiy.presentation.map.uiComponents

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.prilepskiy.common.EMPTY_STRING
import com.prilepskiy.domain.model.PointModel
import com.prilepskiy.presentation.R
import com.yandex.mapkit.geometry.Point

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertDialogAddPoint(
    onPositive: (point: PointModel) -> Unit,
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
        val titlePoint = remember { mutableStateOf(EMPTY_STRING) }
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
                    Text(
                        text = stringResource(id = R.string.add_point),
                    )
                    OutlinedTextField(
                        titlePoint.value,
                        { titlePoint.value = it },
                        label = { Text(stringResource(id = R.string.name_label_point)) },
                        placeholder = { Text(stringResource(id = R.string.name_label_point)) }
                    )
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                    ) {
                        Button(
                            modifier = Modifier,
                            onClick = { onPositive.invoke(point.copy(title = titlePoint.value)) },
                            enabled = titlePoint.value.isNotEmpty()
                        ) {
                            Text(text = stringResource(id = R.string.button_positive))
                        }
                        Button(modifier = Modifier, onClick = { onNegative.invoke() }) {
                            Text(text = stringResource(id = R.string.button_negative))
                        }
                    }
                }
            }
        }
    }
}

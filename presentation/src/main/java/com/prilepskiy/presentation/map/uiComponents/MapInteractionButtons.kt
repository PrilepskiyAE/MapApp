package com.prilepskiy.presentation.map.uiComponents

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.prilepskiy.presentation.R

@Composable
fun MapInteractionButtons(
    modifier: Modifier = Modifier,
    enabledLocation: Boolean,
    zoomPlusClicked: () -> Unit,
    zoomMinusClicked: () -> Unit,
    myLocationClicked: () -> Unit,
) {
    Column(modifier = modifier.padding(end = 16.dp, top = 24.dp)) {

        Image(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .clickable { zoomPlusClicked.invoke() },
            imageVector = ImageVector.vectorResource(id = R.drawable.plus_icon),
            contentDescription = null,
        )

        Image(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .clickable { zoomMinusClicked.invoke() },
            imageVector = ImageVector.vectorResource(id = R.drawable.minus_icon),
            contentDescription = null,
        )
        Image(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .clickable { myLocationClicked.invoke() },
            imageVector = ImageVector.vectorResource(id = if (enabledLocation) R.drawable.my_location_icon else R.drawable.my_location_icon_off),
            contentDescription = null,
        )

    }
}

package com.prilepskiy.presentation.map

import android.content.Context
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import com.prilepskiy.domain.model.PointModel
import com.prilepskiy.presentation.R
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider

fun MapView.changeZoomByStep(value: Float) {
    val smoothAnimation = Animation(Animation.Type.SMOOTH, 0.4f)

    with(mapWindow.map.cameraPosition) {
        mapWindow.map.move(
            CameraPosition(target, zoom + value, azimuth, tilt),
            smoothAnimation,
            null,
        )
    }
}

fun MapView.trackMyPosition(
    context: Context,
): PlacemarkMapObject {
    val drawable = context.getDrawable(R.drawable.mylocation_24)
    val result = mapWindow.map.mapObjects.addPlacemark().apply {
        drawable?.let {
            it.toBitmap().aRGBBitmap { bitmap ->
                setIcon(ImageProvider.fromBitmap(bitmap))
            }
        }
    }
    return result
}

fun MapView.createObject(
    point: PointModel,
): PlacemarkMapObject {
    val result = mapWindow.map.mapObjects.addPlacemark {
        it.geometry = Point(point.latitude,point.longitude)
    }
    return result
}

fun PlacemarkMapObject.redraw(
    context: Context,
    point: PointModel,
) {
        val drawable = context.getDrawable(R.drawable.ic_target)
        drawable?.let {
            it.toBitmap().aRGBBitmap { bitmap ->
                geometry = Point(point.latitude,point.longitude)
                setIcon(ImageProvider.fromBitmap(bitmap))
            }
        }
    }


fun placemarkTapListener(plase: PlacemarkMapObject,onAction: (plase: PlacemarkMapObject) -> Unit) = MapObjectTapListener { _, point ->
    onAction.invoke(plase)
    true
}

private fun Bitmap.aRGBBitmap(result: (Bitmap) -> Unit) {
    this.copy(Bitmap.Config.ARGB_8888, true)?.let {
        result.invoke(it)
    }
}
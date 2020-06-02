package kusu.thegreenway.ui.main.map

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.res.Resources
import android.graphics.PointF
import com.google.type.LatLng
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.IconStyle
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.map.PolylineMapObject
import kusu.thegreenway.R
import kusu.thegreenway.database.models.DotType
import java.lang.RuntimeException

const val ANIMATION_DURATION = 500L
const val DOT_SCALE = 1.0f
const val DOT_SCALE_SELECTED = 1.4f
const val LINE_WIDTH = 2f
const val LINE_WIDTH_SELECTED = 4f

fun DotType?.convertToIcon(): Int {
    return when (this?.id) {
        DotType.ROUTE_START -> R.drawable.ic_pin_a
        DotType.ROUTE_FINISH -> R.drawable.ic_pin_b
        DotType.MEMORIAL -> R.drawable.ic_pin_memorial
        DotType.MUSEUM -> R.drawable.ic_pin_museum
        else -> R.drawable.ic_pin_unknown
    }
}

fun PolylineMapObject?.unselect(resources: Resources) {
    val animatorSet = AnimatorSet()
    animatorSet.playTogether(
        ValueAnimator
            .ofArgb(resources.getColor(R.color.colorAccent), resources.getColor(R.color.colorPrimary))
            .setDuration(ANIMATION_DURATION)
            .apply {
                addUpdateListener { valueAnimator ->
                    this@unselect?.strokeColor = valueAnimator.animatedValue as Int
                }
            },
        ValueAnimator
            .ofFloat(LINE_WIDTH_SELECTED, LINE_WIDTH)
            .setDuration(ANIMATION_DURATION)
            .apply {
                addUpdateListener { valueAnimator ->
                    this@unselect?.strokeWidth = valueAnimator.animatedValue as Float
                }
            })
    animatorSet.start()
}

fun PolylineMapObject?.select(resources: Resources) {
    val animatorSet = AnimatorSet()
    animatorSet.playTogether(
        ValueAnimator
            .ofArgb(resources.getColor(R.color.colorPrimary), resources.getColor(R.color.colorAccent))
            .setDuration(ANIMATION_DURATION)
            .apply {
                addUpdateListener { valueAnimator ->
                    this@select?.strokeColor = valueAnimator.animatedValue as Int
                }
            },
        ValueAnimator
            .ofFloat(LINE_WIDTH, LINE_WIDTH_SELECTED)
            .setDuration(ANIMATION_DURATION)
            .apply {
                addUpdateListener { valueAnimator ->
                    this@select?.strokeWidth = valueAnimator.animatedValue as Float
                }
            })
    animatorSet.start()
}

fun PlacemarkMapObject?.unselect() {
    ValueAnimator
        .ofFloat(DOT_SCALE_SELECTED, DOT_SCALE)
        .setDuration(ANIMATION_DURATION)
        .apply {
            addUpdateListener { valueAnimator ->
                try {
                    this@unselect?.setIconStyle(
                        getBaseIconStyle().setScale(valueAnimator.animatedValue as Float)
                    )
                } catch (e: RuntimeException) {
                    this.cancel()
                }
            }
            start()
        }
}

fun PlacemarkMapObject?.select() {
    ValueAnimator
        .ofFloat(DOT_SCALE, DOT_SCALE_SELECTED)
        .setDuration(ANIMATION_DURATION)
        .apply {
            addUpdateListener { value ->
                this@select?.setIconStyle(
                    getBaseIconStyle().setScale(value.animatedValue as Float)
                )
            }
            start()
        }
}


fun getBaseIconStyle(): IconStyle {
    return IconStyle().apply {
        setAnchor(PointF(0.5f, 1.0f))
    }
}

fun LatLng.toPoint(): Point = Point(this.latitude, this.longitude)
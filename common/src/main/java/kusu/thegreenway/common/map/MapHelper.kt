package kusu.thegreenway.common.map

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PointF
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.animation.addListener
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.*
import kotlinx.android.synthetic.main.el_icon.view.*
import kusu.thegreenway.common.R
import kusu.thegreenway.common.dp
import kusu.thegreenway.common.models.DotType
import java.lang.Exception
import java.lang.RuntimeException

const val ANIMATION_DURATION = 500L
const val DOT_SCALE_ZERO = 0.0f
const val DOT_SCALE = 1.0f
const val DOT_SCALE_SELECTED = 1.4f
const val LINE_WIDTH = 2f
const val LINE_WIDTH_SELECTED = 4f
const val USER_POINT_DURATION = 2000L


fun convertToIcon(context: Context, category: String, icon: String): Bitmap {
    val base = when (category) {
        "accommodation" -> R.drawable.ic_pin_purple
        "attraction" -> R.drawable.ic_pin_yellow
        "etc" -> R.drawable.ic_pin_lblue
        "food" -> R.drawable.ic_pin_green
        "fun" -> R.drawable.ic_pin_blue
        "UNESCO" -> R.drawable.ic_pin_base
        "route" -> {
            when (icon) {
                DotType.ROUTE_START -> R.drawable.ic_pin_a
                DotType.ROUTE_FINISH -> R.drawable.ic_pin_b
                else -> R.drawable.ic_pin_base
            }
        }
        else -> R.drawable.ic_pin_base
    }

    val icon = when (category) {
        "accommodation" -> R.drawable.ic_accomodation
        "attraction" -> R.drawable.ic_atraction
        "etc" -> R.drawable.ic_ets
        "food" -> R.drawable.ic_food
        "fun" -> R.drawable.ic_fun
        "UNESCO" -> R.drawable.ic_unesco
        "route" -> null
        else -> null
    }

    return createDrawableFromView(context, base, icon)
}

private fun createDrawableFromView(context: Context, category: Int, icon: Int?): Bitmap {
    val view = LayoutInflater.from(context).inflate(R.layout.el_icon, null)
    view.icon.setImageResource(category)
    icon?.let {
        view.subIcon.setImageResource(it)
    }
    view.measure(32.dp, 32.dp)
    view.layout(0, 0, 32.dp, 32.dp)
    view.buildDrawingCache()
    val bitmap = Bitmap.createBitmap(32.dp, 32.dp, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    view.draw(canvas)
    return bitmap
}


//fun DotType?.convertToIcon(): Int {
//    return when (this?.id) {
//        DotType.ROUTE_START -> R.drawable.ic_pin_a
//        DotType.ROUTE_FINISH -> R.drawable.ic_pin_b
////        DotType.MEMORIAL -> R.drawable.ic_pin_memorial
////        DotType.MUSEUM -> R.drawable.ic_pin_museum
//
//
////        resting-place
////                artisans_club
////                autocamp
////                bicycle_repair
////                bike_rental
////                cafe
////                camp
////                castle
////                cinema
////                circus
////                coffeehouse
////                farmstead
////                fountain
////                fun_park
////                guesthouse
////                hotel
////                manor
////                memorial
////                monument
////                museum
////                natural_object
////                observation_deck
////                parking
////                pitstop
////                playground
////                route_finish
////                route_start
////                sacred_place
////                sanatorium
////                sports_ground
////                sports_shop
////                sportsequipment_rental
////                store
////                theatre
////                unesco
////                water_source
////                zoo
//
//
//        else -> R.drawable.ic_pin_unknown
//    }
//}

fun PolylineMapObject.unselect(resources: Resources, baseColorId: Int) {
    val animatorSet = AnimatorSet()
    animatorSet.playTogether(
        ValueAnimator
            .ofArgb(resources.getColor(R.color.colorAccent), resources.getColor(baseColorId))
            .setDuration(ANIMATION_DURATION)
            .apply {
                addUpdateListener { valueAnimator ->
                    this@unselect.strokeColor = valueAnimator.animatedValue as Int
                }
            },
        ValueAnimator
            .ofFloat(
                LINE_WIDTH_SELECTED,
                LINE_WIDTH
            )
            .setDuration(ANIMATION_DURATION)
            .apply {
                addUpdateListener { valueAnimator ->
                    this@unselect.strokeWidth = valueAnimator.animatedValue as Float
                }
            })
    animatorSet.start()
}

fun PlacemarkMapObject.moveTo(point: Point) {
    try {
        val start = this.geometry
        val deltaLat = point.latitude - this.geometry.latitude
        val deltaLon = point.longitude - this.geometry.longitude
        ValueAnimator.ofFloat(0f, 1f)
            .setDuration(USER_POINT_DURATION)
            .apply {
                addUpdateListener {
                    try {
                        this@moveTo.geometry = start.plus(deltaLat * it.animatedValue as Float, deltaLon * it.animatedValue as Float)
                    } catch (e: Exception) {

                    }
                }
            }.start()
    } catch (e: Exception) {

    }
}

private fun Point.plus(d: Double, d1: Double): Point {
    return Point(this.latitude + d, this.longitude + d1)
}

fun PolylineMapObject.select(resources: Resources, baseColorId: Int) {
    val animatorSet = AnimatorSet()
    animatorSet.playTogether(
        ValueAnimator
            .ofArgb(resources.getColor(baseColorId), resources.getColor(R.color.colorAccent))
            .setDuration(ANIMATION_DURATION)
            .apply {
                addUpdateListener { valueAnimator ->
                    this@select.strokeColor = valueAnimator.animatedValue as Int
                }
            },
        ValueAnimator
            .ofFloat(
                LINE_WIDTH,
                LINE_WIDTH_SELECTED
            )
            .setDuration(ANIMATION_DURATION)
            .apply {
                addUpdateListener { valueAnimator ->
                    this@select.strokeWidth = valueAnimator.animatedValue as Float
                }
            })
    animatorSet.start()
}

fun PlacemarkMapObject?.unselect() {
    ValueAnimator
        .ofFloat(
            DOT_SCALE_SELECTED,
            DOT_SCALE
        )
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
        .ofFloat(
            DOT_SCALE,
            DOT_SCALE_SELECTED
        )
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

fun PlacemarkMapObject?.addAnimation() {
    ValueAnimator
        .ofFloat(DOT_SCALE_ZERO, DOT_SCALE)
        .setDuration(ANIMATION_DURATION)
        .apply {
            addUpdateListener { value ->
                this@addAnimation?.setIconStyle(
                    getBaseIconStyle().setScale(value.animatedValue as Float)
                )
            }
            start()
        }
}

fun PlacemarkMapObject.removeAnimation(mapObjects: MapObjectCollection) {
    ValueAnimator
        .ofFloat(DOT_SCALE, DOT_SCALE_ZERO)
        .setDuration(ANIMATION_DURATION)
        .apply {
            addUpdateListener { value ->
                this@removeAnimation.setIconStyle(
                    getBaseIconStyle().setScale(value.animatedValue as Float)
                )
            }
            addListener(
                onEnd = {
                    mapObjects.remove(this@removeAnimation)
                }
            )
            start()
        }
}

fun getBaseIconStyle(): IconStyle {
    return IconStyle().apply {
        setAnchor(PointF(0.5f, 1.0f))
    }
}
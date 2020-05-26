package kusu.thegreenway.ui.main.map

import android.content.res.Resources
import com.google.type.LatLng
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.PolylineMapObject
import kusu.thegreenway.R
import kusu.thegreenway.database.models.DotType

fun DotType?.convertToIcon(): Int {
    return when(this?.id){
        DotType.ROUTE_START -> R.drawable.ic_pin_a
        DotType.ROUTE_FINISH -> R.drawable.ic_pin_b
        else -> R.drawable.ic_pin_unknown
    }
}

fun PolylineMapObject?.unselect(resources: Resources) {
    this?.strokeColor = resources.getColor(R.color.colorPrimary)
    this?.strokeWidth = 2f
}

fun PolylineMapObject?.select(resources: Resources) {
    this?.strokeColor = resources.getColor(R.color.colorAccent)
    this?.strokeWidth = 4f
}

fun LatLng.toPoint(): Point = Point(this.latitude, this.longitude)
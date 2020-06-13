package kusu.thegreenway.utils

import com.google.firebase.firestore.GeoPoint
import com.yandex.mapkit.geometry.Point
import java.io.Serializable

class LatLngSerializable(
    val latitude: Double,
    val longitude: Double
) : Serializable {

}

fun GeoPoint.toLatLng(): LatLngSerializable {
    return LatLngSerializable(latitude, longitude)
}

fun LatLngSerializable.toPoint(): Point = Point(this.latitude, this.longitude)

package kusu.thegreenway.common.map

import com.google.firebase.firestore.GeoPoint
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import java.io.Serializable

class CameraPositionSerializable(
    latitude: Double,
    longitude: Double,
    val zoom: Float,
    val azimuth: Float,
    val tilt: Float
) : LatLngSerializable(latitude, longitude) {

    fun toCameraPosition(): CameraPosition {
        return CameraPosition(
            Point(latitude, longitude),
            zoom,
            azimuth,
            tilt
        )
    }

}
package kusu.thegreenway.database.models

import com.google.type.LatLng
import java.io.Serializable

data class Dot(
    val title: String,
    val description: String,
    val type: DotType,
    val position: LatLng
) : Serializable {

    val isRouteSpecific: Boolean = type.onlyRoute
}
package kusu.thegreenway.database.models

import kusu.thegreenway.utils.LatLngSerializable
import java.io.Serializable

data class Dot(
    val id: String,
    val title: String,
    val description: String,
    val type: DotType,
    val position: LatLngSerializable
) : Serializable {

    val isRouteSpecific: Boolean = type.onlyRoute
}
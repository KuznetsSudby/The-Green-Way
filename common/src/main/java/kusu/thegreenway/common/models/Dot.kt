package kusu.thegreenway.common.models

import kusu.thegreenway.common.map.LatLngSerializable
import java.io.Serializable

data class Dot(
    val id: String,
    val isVisible: Boolean,
    val title: String,
    val description: String,
    val type: DotType,
    val position: LatLngSerializable,
    val images: List<String>,
) : Serializable {

    val isRouteSpecific: Boolean = type.onlyRoute
}
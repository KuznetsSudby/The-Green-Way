package kusu.thegreenway.common.models

import java.io.Serializable

data class DotType(
    val id: String,
    val title: String,
    val onlyRoute: Boolean
) : Serializable{

    companion object{
        const val ROUTE_START = "route_start"
        const val ROUTE_FINISH = "route_finish"
        const val MUSEUM = "museum"
        const val MEMORIAL = "memorial"
    }
}
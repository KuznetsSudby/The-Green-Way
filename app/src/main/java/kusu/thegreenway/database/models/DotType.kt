package kusu.thegreenway.database.models

import java.io.Serializable

data class DotType(
    val id: String,
    val description: String
) : Serializable{

    companion object{
        const val ROUTE_START = "route_start"
        const val ROUTE_FINISH = "route_finish"
    }
}
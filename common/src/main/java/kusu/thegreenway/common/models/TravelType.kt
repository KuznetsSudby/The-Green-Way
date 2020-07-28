package kusu.thegreenway.common.models

import java.io.Serializable

data class TravelType(
    val id: String,
    val title: String
): Serializable{


    companion object{
        const val FOOT = "foot"
        const val HORSE = "horse"
        const val BIKE = "bicycle"
        const val SKI = "ski"
        const val RAFTING = "rafting"
    }
}
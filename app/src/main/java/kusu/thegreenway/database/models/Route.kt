package kusu.thegreenway.database.models

import kusu.thegreenway.utils.LatLngSerializable
import java.io.Serializable

data class Route(
    val id: String,
    val title: String,
    val description: String,
    val lines: List<LatLngSerializable>,
    val categories: List<Category>,
    val travelTypes: List<TravelType>,
    val minutes: Int,
    val dots: List<Dot>,
    val difficulty: Difficulty,
    val images: List<String>,
    val animals: Boolean,
    val approved: Boolean,
    val children: Boolean,
    val disabilities: Boolean
) : Serializable
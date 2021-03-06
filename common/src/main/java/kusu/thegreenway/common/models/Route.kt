package kusu.thegreenway.common.models

import kusu.thegreenway.common.map.LatLngSerializable
import java.io.Serializable

data class Route(
    val id: String,
    val isVisible: Boolean,
    val title: String,
    val description: String,
    val lines: List<LatLngSerializable>,
    val categories: List<Category>,
    val travelTypes: List<TravelType>,
    val dots: List<Dot>,
    val difficulty: Difficulty,
    val images: List<String>,
    val animals: Boolean,
    val approved: Boolean,
    val children: Boolean,
    val visuallyImpaired: Boolean,
    val wheelchair: Boolean,
    val distance: Double,
    val durations: HashMap<String, Int>
) : Serializable
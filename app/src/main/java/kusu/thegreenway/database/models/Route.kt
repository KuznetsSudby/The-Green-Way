package kusu.thegreenway.database.models

import com.google.type.LatLng
import java.io.Serializable

data class Route(
    val id: String,
    val title: String,
    val description: String,
    val lines: List<LatLng>,
    val categories: List<Category>,
    val travelTypes: List<TravelType>,
    val dots: List<Dot>,
    val difficulty: Difficulty,
    val images: List<String>,
    val animals: Boolean,
    val approved: Boolean,
    val children: Boolean,
    val disabilities: Boolean
) : Serializable
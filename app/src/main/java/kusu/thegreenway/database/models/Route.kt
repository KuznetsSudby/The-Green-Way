package kusu.thegreenway.database.models

import com.google.type.LatLng
import java.io.Serializable

data class Route(
    val id: String,
    val title: String,
    val description: String,
    val lines: List<LatLng>,
    val categories: List<Category>,
    val dots: List<Dot>
) : Serializable
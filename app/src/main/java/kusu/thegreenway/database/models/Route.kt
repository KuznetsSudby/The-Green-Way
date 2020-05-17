package kusu.thegreenway.database.models

import com.google.type.LatLng
import java.io.Serializable

data class Route(
    val title: String,
    val description: String,
    val lines: List<LatLng>,
    val categories: List<Reference>,
    val dots: List<Reference>
) : Serializable
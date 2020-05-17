package kusu.thegreenway.database.firestore.models

import com.google.firebase.firestore.GeoPoint
import com.google.type.LatLng
import java.io.Serializable

data class FirestoreDot(
    val title: String,
    val description: String,
    val type: String,
    val position: GeoPoint
): Serializable
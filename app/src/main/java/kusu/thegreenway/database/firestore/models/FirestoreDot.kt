package kusu.thegreenway.database.firestore.models

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.GeoPoint
import java.io.Serializable

class FirestoreDot: Serializable {
    val title: String = ""
    val description: String = ""
    val type: DocumentReference? = null
    val position: GeoPoint = GeoPoint(0.0, 0.0)
}
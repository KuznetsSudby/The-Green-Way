package kusu.thegreenway.database.firestore.models

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.GeoPoint
import java.io.Serializable

class FirestoreRoute : Serializable {
    val title: String = ""
    val description: String = ""
    val lines: List<GeoPoint> = emptyList()
    val categories: List<DocumentReference> = emptyList()
    val dots: List<DocumentReference> = emptyList()
}
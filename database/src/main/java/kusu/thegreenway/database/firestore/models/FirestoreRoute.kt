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
    val types: List<DocumentReference> = emptyList()
    val difficulty: DocumentReference? = null
    val images: List<String> = emptyList()
    val animals: Boolean = false
    val approved: Boolean = false
    val children: Boolean = false
    val visuallyImpaired: Boolean = false
    val wheelchair: Boolean = false
    val distance: Double = 1.0
    val durations = HashMap<String, Int>()
    val isVisible: Boolean = true
}
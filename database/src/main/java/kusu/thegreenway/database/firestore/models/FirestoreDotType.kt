package kusu.thegreenway.database.firestore.models

import com.google.firebase.firestore.DocumentReference
import java.io.Serializable

class FirestoreDotType : Serializable {
    val title: String = ""
    val onlyRoute: Boolean = false
    val category: DocumentReference? = null
}
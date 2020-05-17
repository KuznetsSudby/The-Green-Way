package kusu.thegreenway.database.firestore

import android.util.Log
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.type.LatLng
import kotlinx.coroutines.tasks.await
import kusu.thegreenway.database.firestore.models.FirestoreCategory
import kusu.thegreenway.database.firestore.models.FirestoreDot
import kusu.thegreenway.database.firestore.models.FirestoreDotType
import kusu.thegreenway.database.firestore.models.FirestoreRoute
import kusu.thegreenway.database.models.*
import kusu.thegreenway.utils.Result

class FirestoreDB : DB {
    val db = Firebase.firestore

    override suspend fun getRoutes(): Result<HashMap<String, Route>> {
        try {
            val result = db.collection(ROUTES)
                .get()
                .await()
                .documents

            val map = HashMap<String, Route>()
            result.forEach { document ->
                val converted = document.toObject(FirestoreRoute::class.java)!!
                map[document.id] = Route(
                    title = converted.title,
                    description = converted.description,
                    lines = converted.lines.map { it.toLatLng() },
                    dots = converted.dots.map { Reference(it.id) },
                    categories = converted.categories.map { Reference(it.id) }
                )
            }
            return Result.Success(map)
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }

    override suspend fun getCategories(): Result<HashMap<String, Category>> {
        try {
            val result = db.collection(CATEGORIES)
                .get()
                .await()
                .documents

            val map = HashMap<String, Category>()
            result.forEach { document ->
                val converted = document.toObject(FirestoreCategory::class.java)!!
                map[document.id] = Category(
                    title = converted.title,
                    description = converted.description
                )
            }
            return Result.Success(map)
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }

    override suspend fun getDots(): Result<HashMap<String, Dot>> {
        try {
            val result = db.collection(DOTS)
                .get()
                .await()
                .documents

            val map = HashMap<String, Dot>()
            result.forEach { document ->
                val converted = document.toObject(FirestoreDot::class.java)!!
                map[document.id] = Dot(
                    title = converted.title,
                    description = converted.description,
                    position = converted.position.toLatLng(),
                    type = Reference(converted.type)
                )
            }
            return Result.Success(map)
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }

    override suspend fun getDotTypes(): Result<HashMap<String, DotType>> {
        try {
            val result = db.collection(DOT_TYPES)
                .get()
                .await()
                .documents

            val map = HashMap<String, DotType>()
            result.forEach { document ->
                val converted = document.toObject(FirestoreDotType::class.java)!!
                map[document.id] = DotType(
                    description = converted.description
                )
            }
            return Result.Success(map)
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }


    companion object {
        const val TAG = "ExcM FirestoreDB"
        const val ROUTES = "routes"
        const val CATEGORIES = "categories"
        const val DOT_TYPES = "dot_types"
        const val DOTS = "dots"
    }
}

fun GeoPoint.toLatLng(): LatLng {
    return LatLng.newBuilder().setLatitude(latitude).setLongitude(longitude).build()
}
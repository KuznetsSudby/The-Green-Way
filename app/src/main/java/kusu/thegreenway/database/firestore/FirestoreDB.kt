package kusu.thegreenway.database.firestore

import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.type.LatLng
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import kusu.thegreenway.database.DB
import kusu.thegreenway.database.firestore.models.FirestoreCategory
import kusu.thegreenway.database.firestore.models.FirestoreDot
import kusu.thegreenway.database.firestore.models.FirestoreDotType
import kusu.thegreenway.database.firestore.models.FirestoreRoute
import kusu.thegreenway.database.models.*
import kusu.thegreenway.utils.Result
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreDB @Inject constructor() : DB {

    val db = Firebase.firestore

    val job = SupervisorJob()
    val scope = CoroutineScope(Dispatchers.IO + job)

    override suspend fun getRoutes(): Result<List<Route>> {
        try {
            val jobs = ArrayList<Deferred<Any>>()

            val jobRoutes = scope.async(Dispatchers.IO) {
                db.collection(ROUTES)
                    .get()
                    .await()
                    .documents
            }.apply { jobs.add(this) }
            val jobCategories = scope.async(Dispatchers.IO) {
                db.collection(CATEGORIES)
                    .get()
                    .await()
                    .documents
            }.apply { jobs.add(this) }
            val jobDots = scope.async(Dispatchers.IO) {
                db.collection(DOTS)
                    .get()
                    .await()
                    .documents
            }.apply { jobs.add(this) }
            val jobDotTypes = scope.async(Dispatchers.IO) {
                db.collection(DOT_TYPES)
                    .get()
                    .await()
                    .documents
            }.apply { jobs.add(this) }

            jobs.awaitAll().find { it is Result.Error }?.let {
                return it as Result.Error
            }

            val types = HashMap<String, DotType>()
            jobDotTypes.getCompleted().forEach { document ->
                val converted = document.toObject(FirestoreDotType::class.java)!!
                types[document.id] = DotType(
                    id = document.id,
                    description = converted.description
                )
            }

            val categories = HashMap<String, Category>()
            jobDotTypes.getCompleted().forEach { document ->
                val converted = document.toObject(FirestoreCategory::class.java)!!
                categories[document.id] = Category(
                    document.id,
                    title = converted.title,
                    description = converted.description
                )
            }

            val dots = HashMap<String, Dot>()
            jobDots.getCompleted().forEach { document ->
                val converted = document.toObject(FirestoreDot::class.java)!!
                dots[document.id] = Dot(
                    title = converted.title,
                    description = converted.description,
                    position = converted.position.toLatLng(),
                    type = types[converted.type?.id]
                )
            }

            return Result.Success(jobRoutes.getCompleted().map { document ->
                val converted = document.toObject(FirestoreRoute::class.java)!!
                Route(
                    id = document.id,
                    title = converted.title,
                    description = converted.description,
                    lines = converted.lines.map { it.toLatLng() },
                    dots = converted.dots.map { dots[it.id] }.filterNotNull(),
                    categories = converted.categories.map { categories[it.id] }.filterNotNull()
                )
            })
        } catch (e: Exception) {
            return Result.Error(e)
        }
    }


    companion object {
        const val ROUTES = "routes"
        const val CATEGORIES = "categories"
        const val DOT_TYPES = "dot_types"
        const val DOTS = "dots"
    }
}

fun GeoPoint.toLatLng(): LatLng {
    return LatLng.newBuilder().setLatitude(latitude).setLongitude(longitude).build()
}
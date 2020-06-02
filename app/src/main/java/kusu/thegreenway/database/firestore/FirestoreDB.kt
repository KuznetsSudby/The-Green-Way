package kusu.thegreenway.database.firestore

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import com.google.type.LatLng
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import kusu.thegreenway.database.DB
import kusu.thegreenway.database.firestore.models.*
import kusu.thegreenway.database.models.*
import kusu.thegreenway.utils.Event
import kusu.thegreenway.utils.Result
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreDB @Inject constructor() : DB {

    private val firestore = Firebase.firestore.apply {
        firestoreSettings = firestoreSettings {
            isPersistenceEnabled = true
        }
    }

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    private val types = HashMap<String, DotType>()
    private val travelTypes = HashMap<String, TravelType>()
    private val categories = HashMap<String, Category>()
    private val difficulties = HashMap<String, Difficulty>()
    private val dotsMap = HashMap<String, Dot>()
    private val routesMap = HashMap<String, Route>()

    private val state = MutableLiveData<State>(State.NO_DATA)
    private val _dots = MutableLiveData<List<Dot>>()
    private val _routes = MutableLiveData<List<Route>>()
    private val _exception = MutableLiveData<Event<Exception>>()

    override val loading: LiveData<Boolean> = Transformations.map(state) {
        it == State.LOADING
    }

    override val dots: LiveData<List<Dot>> = _dots
    override val routes: LiveData<List<Route>> = _routes
    override val exception: LiveData<Event<Exception>> = _exception

    //TODO add Source.CACHE logic
    override suspend fun loadData() {
        synchronized(state) {
            if (state.value != State.NO_DATA)
                return
            state.postValue(State.LOADING)
        }

        val jobs = ArrayList<Deferred<Any>>()
        val jobRoutes = scope.async(Dispatchers.IO) {
            firestore.collection(ROUTES)
                .get()
                .await()
                .documents
        }.apply { jobs.add(this) }
        val jobCategories = scope.async(Dispatchers.IO) {
            firestore.collection(CATEGORIES)
                .get()
                .await()
                .documents
        }.apply { jobs.add(this) }
        val jobDots = scope.async(Dispatchers.IO) {
            firestore.collection(DOTS)
                .get()
                .await()
                .documents
        }.apply { jobs.add(this) }
        val jobDotTypes = scope.async(Dispatchers.IO) {
            firestore.collection(DOT_TYPES)
                .get()
                .await()
                .documents
        }.apply { jobs.add(this) }
        val jobTravelTypes = scope.async(Dispatchers.IO) {
            firestore.collection(TRAVEL_TYPES)
                .get()
                .await()
                .documents
        }.apply { jobs.add(this) }
        val jobDifficulties = scope.async(Dispatchers.IO) {
            firestore.collection(DIFFICULTIES)
                .get()
                .await()
                .documents
        }.apply { jobs.add(this) }

        jobs.awaitAll().find { it is Result.Error }?.let {
            _exception.postValue(Event((it as Result.Error).exception))
            synchronized(state) {
                state.postValue(State.NO_DATA)
            }
            return
        }

        jobDotTypes.getCompleted().forEach { document ->
            val converted = document.toObject(FirestoreDotType::class.java)!!
            types[document.id] = DotType(
                id = document.id,
                title = converted.title,
                onlyRoute = converted.onlyRoute
            )
        }

        jobTravelTypes.getCompleted().forEach { document ->
            val converted = document.toObject(FirestoreTravelType::class.java)!!
            travelTypes[document.id] = TravelType(
                id = document.id,
                title = converted.title
            )
        }

        jobCategories.getCompleted().forEach { document ->
            val converted = document.toObject(FirestoreCategory::class.java)!!
            categories[document.id] = Category(
                document.id,
                title = converted.title
            )
        }

        jobDifficulties.getCompleted().forEach { document ->
            val converted = document.toObject(FirestoreDifficulty::class.java)!!
            difficulties[document.id] = Difficulty(
                document.id,
                title = converted.title
            )
        }

        jobDots.getCompleted().forEach { document ->
            val converted = document.toObject(FirestoreDot::class.java)!!
            dotsMap[document.id] = Dot(
                title = converted.title,
                description = converted.description,
                position = converted.position.toLatLng(),
                type = types[converted.type?.id] ?: return@forEach
            )
        }

        jobRoutes.getCompleted().map { document ->
            val converted = document.toObject(FirestoreRoute::class.java)!!
            routesMap[document.id] = Route(
                id = document.id,
                title = converted.title,
                description = converted.description,
                lines = converted.lines.map { it.toLatLng() },
                dots = converted.dots.map { dotsMap[it.id] }.filterNotNull(),
                categories = converted.categories.map { categories[it.id] }.filterNotNull(),
                difficulty = difficulties[converted.difficulty?.id ?: ""] ?: return@map,
                travelTypes = converted.travelTypes.map { travelTypes[it.id] }.filterNotNull(),
                images = converted.images,
                animals = converted.animals,
                approved = converted.approved,
                children = converted.children,
                disabilities = converted.disabilities
            )
        }

        _dots.postValue(dotsMap.values.toList())
        _routes.postValue(routesMap.values.toList())

        synchronized(state) {
            state.postValue(State.LOADED)
        }
    }


    companion object {
        const val ROUTES = "routes"
        const val CATEGORIES = "categories"
        const val DOT_TYPES = "dot_types"
        const val TRAVEL_TYPES = "travel_types"
        const val DIFFICULTIES = "difficulties"
        const val DOTS = "dots"
    }
}

enum class State {
    NO_DATA,
    LOADING,
    LOADED
}

fun GeoPoint.toLatLng(): LatLng {
    return LatLng.newBuilder().setLatitude(latitude).setLongitude(longitude).build()
}
package kusu.thegreenway.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import kusu.thegreenway.database.firestore.DB
import kusu.thegreenway.database.firestore.FirestoreDB
import kusu.thegreenway.database.models.Category
import kusu.thegreenway.database.models.Dot
import kusu.thegreenway.database.models.DotType
import kusu.thegreenway.database.models.Route
import kotlin.collections.HashMap
import kusu.thegreenway.utils.Result

object DataBaseManager {

    val job = SupervisorJob()
    val scope = CoroutineScope(Dispatchers.IO + job)

    val db: DB = FirestoreDB()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _exception = MutableLiveData<Exception>()
    val exception: LiveData<Exception> = _exception

    var isLoaded: Boolean = false

    val categories = HashMap<String, Category>()
    val dotTypes = HashMap<String, DotType>()
    val dots = HashMap<String, Dot>()
    val routes = HashMap<String, Route>()

    fun loadData() {
        scope.launch {
            _isLoading.postValue(true)
            isLoaded = false

            val jobs = ArrayList<Deferred<Any>>()

            val jobRoutes = async(Dispatchers.IO) { db.getRoutes() }.apply { jobs.add(this) }
            val jobCategories = async(Dispatchers.IO) { db.getCategories() }.apply { jobs.add(this) }
            val jobDots = async(Dispatchers.IO) { db.getDots() }.apply { jobs.add(this) }
            val jobDotTypes = async(Dispatchers.IO) { db.getDotTypes() }.apply { jobs.add(this) }

            jobs.awaitAll().find { it is Result.Error }?.let {
                isLoaded = false
                _exception.postValue((it as Result.Error).exception)
            } ?: run {
                isLoaded = true
                routes.clearAndAdd(jobRoutes.getCompleted())
                categories.clearAndAdd(jobCategories.getCompleted())
                dots.clearAndAdd(jobDots.getCompleted())
                dotTypes.clearAndAdd(jobDotTypes.getCompleted())
            }

            _isLoading.postValue(false)
        }
    }
}

private fun <K, V> HashMap<K, V>.clearAndAdd(completed: Result<HashMap<K, V>>) {
    clear()
    if (completed is Result.Success)
        putAll(completed.data)
}

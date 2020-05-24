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
import kusu.thegreenway.utils.Event
import kotlin.collections.HashMap
import kusu.thegreenway.utils.Result

object DataBaseManager {

    val job = SupervisorJob()
    val scope = CoroutineScope(Dispatchers.IO + job)

    val db: DB = FirestoreDB()
    var isLoaded: Boolean = false

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _exception = MutableLiveData<Event<Exception>>()
    val exception: LiveData<Event<Exception>> = _exception

    private val _routes = MutableLiveData<List<Route>>()
    val routes: LiveData<List<Route>> = _routes

    fun loadData() {
        if (isLoaded || isLoading.value == true)
            return
        scope.launch {
            _isLoading.postValue(true)
            isLoaded = false

            val result = db.getRoutes()
            result.proceedResult(
                success = {
                    isLoaded = true
                    _routes.postValue(it)
                },
                error = {
                    isLoaded = false
                    _exception.postValue(Event(it))
                }
            )
            _isLoading.postValue(false)
        }
    }
}


package kusu.thegreenway.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import kotlinx.coroutines.*
import kusu.thegreenway.database.models.Dot
import kusu.thegreenway.database.models.Route
import kusu.thegreenway.utils.Event
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataBaseManager @Inject constructor(val db: DB) {

    val job = SupervisorJob()
    val scope = CoroutineScope(Dispatchers.IO + job)

    val loading: LiveData<Boolean> = db.loading
    val exception: LiveData<Event<Exception>> = db.exception

    val routes: LiveData<List<Route>> = db.routes
    val dots: LiveData<List<Dot>> = db.dots

    fun loadData() {
        scope.launch {
            db.loadData()
        }
    }
}


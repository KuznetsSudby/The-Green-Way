package kusu.thegreenway.database

import androidx.lifecycle.LiveData
import kotlinx.coroutines.*
import kusu.thegreenway.database.models.Dot
import kusu.thegreenway.database.models.Route
import kusu.thegreenway.utils.Event
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseManager @Inject constructor(val database: Database) {

    val job = SupervisorJob()
    val scope = CoroutineScope(Dispatchers.IO + job)

    val loading: LiveData<Boolean> = database.loading
    val exception: LiveData<Event<Exception>> = database.exception

    val routes: LiveData<List<Route>> = database.routes
    val dots: LiveData<List<Dot>> = database.dots

    fun loadData() {
        scope.launch {
            database.loadData()
        }
    }
}


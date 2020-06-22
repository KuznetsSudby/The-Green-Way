package kusu.thegreenway.database

import androidx.lifecycle.LiveData
import kusu.thegreenway.database.models.Dot
import kusu.thegreenway.database.models.Route
import kusu.thegreenway.utils.Event

interface DB {

    val routes: LiveData<List<Route>>
    val dots: LiveData<List<Dot>>

    val loading: LiveData<Boolean>
    val exception: LiveData<Event<Exception>>

    suspend fun loadData()
}
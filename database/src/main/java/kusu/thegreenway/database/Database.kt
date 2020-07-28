package kusu.thegreenway.database

import androidx.lifecycle.LiveData
import kusu.thegreenway.common.models.Dot
import kusu.thegreenway.common.models.Route
import kusu.thegreenway.common.Event

interface Database {

    val routes: LiveData<List<Route>>
    val dots: LiveData<List<Dot>>

    val loading: LiveData<Boolean>
    val exception: LiveData<Event<Exception>>

    suspend fun loadData()
}
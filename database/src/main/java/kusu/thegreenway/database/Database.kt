package kusu.thegreenway.database

import androidx.lifecycle.LiveData
import kusu.thegreenway.common.models.Dot
import kusu.thegreenway.common.models.Route
import kusu.thegreenway.common.Event
import kusu.thegreenway.common.models.Category
import kusu.thegreenway.common.models.TravelType
import java.lang.reflect.Array

interface Database {

    val routes: LiveData<List<Route>>
    val dots: LiveData<List<Dot>>

    val travelTypesList: ArrayList<TravelType>
    val categoriesList: ArrayList<Category>

    val loading: LiveData<Boolean>
    val exception: LiveData<Event<Exception>>

    suspend fun loadData()
}
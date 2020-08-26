package kusu.thegreenway.ui.routes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kusu.thegreenway.common.CombineLiveData
import kusu.thegreenway.database.DatabaseManager
import kusu.thegreenway.favorites.FavoritesModel
import javax.inject.Inject

class RoutesViewModel @Inject constructor(
    dbManger: DatabaseManager,
    val favoritesModel: FavoritesModel
) : ViewModel() {

    private val _search = MutableLiveData<String>("")
    val search: LiveData<String> = _search

    private val routes = dbManger.routes
    val dbException = dbManger.exception

    val screenRoutes = CombineLiveData(routes, _search) { routes, search ->
        routes?.filter { it.title.contains(search ?: "", true) } ?: emptyList()
    }

    init {
        dbManger.loadData()
    }

    fun setSearch(value: String) {
        _search.value = value
    }

    fun clearSearch() {
        _search.value = ""
    }
}

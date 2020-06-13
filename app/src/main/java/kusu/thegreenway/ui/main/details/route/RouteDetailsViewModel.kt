package kusu.thegreenway.ui.main.details.route

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kusu.thegreenway.database.models.Dot
import kusu.thegreenway.database.models.Route
import kusu.thegreenway.ui.main.favorites.FavoritesModel
import javax.inject.Inject

class RouteDetailsViewModel @Inject constructor(
    val favoritesModel: FavoritesModel
) : ViewModel() {

    private val _route = MutableLiveData<Route>()
    val route: LiveData<Route> = _route

    fun loadDetails(route: Route) {
        _route.postValue(route)
    }
}

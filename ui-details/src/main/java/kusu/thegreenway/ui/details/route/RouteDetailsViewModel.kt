package kusu.thegreenway.ui.details.route

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kusu.thegreenway.common.models.Route
import kusu.thegreenway.favorites.FavoritesModel
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

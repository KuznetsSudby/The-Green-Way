package kusu.thegreenway.ui.details.route

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kusu.thegreenway.common.models.Route
import kusu.thegreenway.common.models.TravelType
import kusu.thegreenway.favorites.FavoritesModel
import javax.inject.Inject

class RouteDetailsViewModel @Inject constructor(
    val favoritesModel: FavoritesModel
) : ViewModel() {

    private val _route = MutableLiveData<Route>()
    val route: LiveData<Route> = _route

    private val _selectedType = MutableLiveData<TravelType>()
    val selectedType: LiveData<TravelType> = _selectedType

    init {
        _route.observeForever {
            if (it.travelTypes.isNotEmpty() && !it.travelTypes.contains(selectedType.value)){
                _selectedType.postValue(it.travelTypes[0])
            }
        }
    }

    fun loadDetails(route: Route) {
        _route.postValue(route)
    }

    fun selectType(trType: TravelType) {
        _selectedType.postValue(trType)
    }
}

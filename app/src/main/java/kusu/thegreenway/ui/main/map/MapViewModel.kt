package kusu.thegreenway.ui.main.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yandex.mapkit.map.MapObject
import com.yandex.mapkit.map.PolylineMapObject
import kusu.thegreenway.database.DataBaseManager
import kusu.thegreenway.database.models.Route
import javax.inject.Inject

class MapViewModel @Inject constructor(dbManger: DataBaseManager) : ViewModel() {

    val dbLoading = dbManger.loading
    val dbException = dbManger.exception

    val routes = dbManger.routes
    val dots = dbManger.dots

    private var _selectedItem: MutableLiveData<Route?> = MutableLiveData()
    var selectedItem: LiveData<Route?> = _selectedItem

    init {
        dbManger.loadData()
    }

    fun selectRoute(route: Route?) {
        _selectedItem.postValue(route)
    }
}

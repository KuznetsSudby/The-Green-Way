package kusu.thegreenway.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yandex.mapkit.map.MapObject
import com.yandex.mapkit.map.PolylineMapObject
import kusu.thegreenway.database.DataBaseManager
import kusu.thegreenway.database.models.Route

class MainViewModel : ViewModel() {

    val dbLoading = DataBaseManager.isLoading
    val dbException = DataBaseManager.exception

    val routes = DataBaseManager.routes

    private var _selectedItem : MutableLiveData<Route?> = MutableLiveData()
    var selectedItem : LiveData<Route?> = _selectedItem

    init {
        DataBaseManager.loadData()
    }

    fun selectRoute(route: Route?) {
        _selectedItem.postValue(route)
    }
}

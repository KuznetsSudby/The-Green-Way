package kusu.thegreenway.ui.main.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yandex.mapkit.map.CameraPosition
import kusu.thegreenway.utils.CombineLiveData
import kusu.thegreenway.database.DatabaseManager
import kusu.thegreenway.database.models.Dot
import kusu.thegreenway.database.models.Route
import kusu.thegreenway.ui.main.favorites.FavoritesModel
import javax.inject.Inject

class MapViewModel @Inject constructor(
    dbManger: DatabaseManager,
    val favoritesModel: FavoritesModel
) : ViewModel() {

    val dbLoading = dbManger.loading
    val dbException = dbManger.exception

    val routes = dbManger.routes
    val dots = dbManger.dots

    private val _cameraPosition: MutableLiveData<CameraPosition?> = MutableLiveData(null)
    val cameraPosition: LiveData<CameraPosition?> = _cameraPosition

    private val _selectedItem: MutableLiveData<Route?> = MutableLiveData()
    val selectedItem: LiveData<Route?> = _selectedItem

    private val _selectedDotItem: MutableLiveData<Dot?> = MutableLiveData()
    val selectedDotItem: LiveData<Dot?> = _selectedDotItem

    val showDescription = CombineLiveData(selectedDotItem, selectedItem) { first, second ->
        first != null || second != null
    }

    init {
        dbManger.loadData()
    }

    fun selectRoute(route: Route?) {
        _selectedItem.postValue(route)
        selectDot(null)
    }

    fun selectDot(dot: Dot?) {
        _selectedDotItem.postValue(dot)
    }

    fun getSelectedObject(): Any? {
        return selectedDotItem.value ?: selectedItem.value
    }

    fun savePosition(position: CameraPosition) {
        _cameraPosition.postValue(position)
    }
}

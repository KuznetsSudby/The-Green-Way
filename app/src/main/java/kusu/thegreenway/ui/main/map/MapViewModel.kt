package kusu.thegreenway.ui.main.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kusu.thegreenway.utils.CombineLiveData
import kusu.thegreenway.database.DataBaseManager
import kusu.thegreenway.database.models.Dot
import kusu.thegreenway.database.models.Route
import javax.inject.Inject

class MapViewModel @Inject constructor(dbManger: DataBaseManager) : ViewModel() {

    val dbLoading = dbManger.loading
    val dbException = dbManger.exception

    val routes = dbManger.routes
    val dots = dbManger.dots

    private var _selectedItem: MutableLiveData<Route?> = MutableLiveData()
    var selectedItem: LiveData<Route?> = _selectedItem

    private var _selectedDotItem: MutableLiveData<Dot?> = MutableLiveData()
    var selectedDotItem: LiveData<Dot?> = _selectedDotItem

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

    fun getDescriptionText(): String {
        return selectedDotItem.value?.title ?: selectedItem.value?.title ?: ""
    }
}

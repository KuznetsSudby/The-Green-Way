package kusu.thegreenway.ui.routes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kusu.thegreenway.common.models.Route
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

    val typesList = dbManger.travelTypesList
    val categoriesList = dbManger.categoriesList

    var types = DEF_TYPES.clone() as HashSet<String>
    var categories = DEF_CATEGORIES.clone() as HashSet<String>
    var availabilities = DEF_AVAILABILITY.clone() as HashSet<String>
    var range: Pair<Int, Int> = DEF_RANGE.copy()


    private val _screenRoutes = MutableLiveData<List<Route>>()
    val screenRoutes: LiveData<List<Route>> = _screenRoutes

    init {
        dbManger.loadData()
        search.observeForever {
            updateList()
        }
    }

    fun setSearch(value: String) {
        _search.value = value
    }

    fun clearSearch() {
        _search.value = ""
    }

    fun saveFilter(
        selectedTypes: HashSet<String>,
        selectedCategories: HashSet<String>,
        selectedAvailabilities: HashSet<String>,
        selectedRange: Pair<Int, Int>
    ) {
        categories = selectedCategories
        availabilities = selectedAvailabilities
        types = selectedTypes
        range = selectedRange

        updateList()
    }

    private fun updateList() {
        var filtered = routes.value?.filter { it.title.contains(search.value ?: "", true) } ?: emptyList()
        if (categories.isNotEmpty()) {
            filtered = filtered.filter { it.categories.any { categories.contains(it.id) } }
        }
        if (types.isNotEmpty()) {
            filtered = filtered.filter { it.travelTypes.any { types.contains(it.id) } }
        }
        if (availabilities.isNotEmpty()) {
            filtered = filtered.filter {
                (availabilities.contains(Availability.Child.id) == it.children || it.children)
                        && (availabilities.contains(Availability.Animal.id) == it.animals || it.animals)
                        && (availabilities.contains(Availability.Wheel.id) == it.wheelchair || it.wheelchair)
                        && (availabilities.contains(Availability.Visual.id) == it.visuallyImpaired || it.visuallyImpaired)
            }
        }
        filtered = filtered.filter { it.distance >= range.first && (it.distance <= range.second || range.second == DEF_RANGE.second) }

        _screenRoutes.postValue(filtered)
    }

    fun isFiltered(
        selectedTypes: HashSet<String> = types,
        selectedCategories: HashSet<String> = categories,
        selectedAvailabilities: HashSet<String> = availabilities,
        selectedRange: Pair<Int, Int> = range
    ): Boolean {
        return selectedTypes.size != DEF_TYPES.size
                || selectedCategories.size != DEF_CATEGORIES.size
                || selectedAvailabilities.size != DEF_AVAILABILITY.size
                || selectedRange.first != DEF_RANGE.first
                || selectedRange.second != DEF_RANGE.second
                || !selectedTypes.all { DEF_TYPES.contains(it) }
                || !selectedCategories.all { DEF_CATEGORIES.contains(it) }
                || !selectedAvailabilities.all { DEF_AVAILABILITY.contains(it) }
    }

    companion object {
        val DEF_TYPES = HashSet<String>()
        val DEF_CATEGORIES = HashSet<String>()
        val DEF_AVAILABILITY = HashSet<String>()
        var DEF_RANGE = Pair(1, 100)
    }
}

enum class Availability(val id: String, val title: Int) {
    Child("child", R.string.ui_with_child_min),
    Animal("animal", R.string.ui_with_animal_min),
    Wheel("wheel", R.string.ui_with_wheelchair_min),
    Visual("visual", R.string.ui_with_visually_impaired_min)
}

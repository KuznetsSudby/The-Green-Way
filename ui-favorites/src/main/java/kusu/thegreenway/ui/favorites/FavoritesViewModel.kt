package kusu.thegreenway.ui.favorites

import androidx.lifecycle.ViewModel
import kusu.thegreenway.database.DatabaseManager
import kusu.thegreenway.favorites.FavoritesModel
import javax.inject.Inject

class FavoritesViewModel @Inject constructor(
    dbManger: DatabaseManager,
    val favoritesModel: FavoritesModel
) : ViewModel() {

    val routes = dbManger.routes
    val dbException = dbManger.exception

    init {
        dbManger.loadData()
    }

}

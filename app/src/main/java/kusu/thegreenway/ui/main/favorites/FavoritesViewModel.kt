package kusu.thegreenway.ui.main.favorites

import androidx.lifecycle.ViewModel
import kusu.thegreenway.database.DataBaseManager
import javax.inject.Inject

class FavoritesViewModel @Inject constructor(
    dbManger: DataBaseManager,
    val favoritesModel: FavoritesModel
) : ViewModel() {

    val routes = dbManger.routes
    val dbException = dbManger.exception

    init {
        dbManger.loadData()
    }

}

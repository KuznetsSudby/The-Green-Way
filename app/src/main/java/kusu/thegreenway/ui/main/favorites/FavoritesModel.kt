package kusu.thegreenway.ui.main.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.android.synthetic.main.f_map.*
import kusu.thegreenway.R
import kusu.thegreenway.app.App
import kusu.thegreenway.auth.AuthRepository
import kusu.thegreenway.database.models.Route
import kusu.thegreenway.favorites.FavoritesRepository
import kusu.thegreenway.utils.Event
import javax.inject.Inject

class FavoritesModel @Inject constructor(
    val app: App,
    val favoritesRepository: FavoritesRepository,
    val authRepository: AuthRepository
) {

    private val _message: MutableLiveData<Event<String>> = MutableLiveData()
    val message: LiveData<Event<String>> = _message

    fun isRouteFavorite(route: Route): Boolean {
        return authRepository.isAuth.value == true && favoritesRepository.isContainsRoute(route.id)
    }

    fun changeFavorite(route: Route) {
        if (authRepository.isAuth.value == true) {
            favoritesRepository.changeRoute(route.id)
        } else {
            _message.postValue(Event(app.getString(R.string.ui_need_auth)))
        }
    }

    fun toResource(route: Route): Int {
        return if (isRouteFavorite(route))
            R.drawable.ic_star_selected
        else
            R.drawable.ic_star
    }

    fun toColor(route: Route): Int {
        return if (isRouteFavorite(route))
            R.color.colorFavorite
        else
            R.color.colorPrimary
    }
}
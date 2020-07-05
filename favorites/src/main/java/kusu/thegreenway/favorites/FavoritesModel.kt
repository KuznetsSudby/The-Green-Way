package kusu.thegreenway.favorites

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kusu.thegreenway.auth.AuthRepository
import kusu.thegreenway.common.Event
import kusu.thegreenway.common.models.Route
import javax.inject.Inject

class FavoritesModel @Inject constructor(
    val app: Application,
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
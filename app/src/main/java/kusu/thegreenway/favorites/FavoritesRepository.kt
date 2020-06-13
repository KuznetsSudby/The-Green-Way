package kusu.thegreenway.favorites

import android.app.Activity
import kusu.thegreenway.BuildConfig
import kusu.thegreenway.app.App
import kusu.thegreenway.auth.AuthRepository
import kusu.thegreenway.preferences.PreferenceStringSet
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoritesRepository @Inject constructor(
    val authRepository: AuthRepository,
    val app: App
) {

    init {
        authRepository.firebaseUserLiveData.observeForever {
            reloadList()
        }
    }

    var favoriteRoutes = getFavoriteRoutesPreference()

    private fun reloadList() {
        favoriteRoutes = getFavoriteRoutesPreference()
    }

    private fun getFavoriteRoutesPreference(): PreferenceStringSet {
        return PreferenceStringSet(
            key = PREF_ROUTES_LIST + authRepository.uid,
            store = app.getSharedPreferences(BuildConfig.APPLICATION_ID, Activity.MODE_PRIVATE))
    }

    fun isContainsRoute(id: String) = favoriteRoutes.value.contains(id)

    fun addRoute(id: String){
        favoriteRoutes.value = favoriteRoutes.value + id
    }

    fun removeRoute(id: String){
        favoriteRoutes.value = favoriteRoutes.value - id
    }

    fun changeRoute(id: String) {
        if (isContainsRoute(id))
            removeRoute(id)
        else
            addRoute(id)
    }

    companion object{
        const val PREF_ROUTES_LIST = "pref_routes_list_"
    }
}
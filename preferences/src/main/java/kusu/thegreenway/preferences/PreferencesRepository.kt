package kusu.thegreenway.preferences

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kusu.thegreenway.auth.AuthRepository
import kusu.thegreenway.common.dagger.Config
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesRepository @Inject constructor(
    val authRepository: AuthRepository,
    val app: Application,
    val config: Config
) {

    private val _userPreferencesUpdated = MutableLiveData<Unit>()
    val userPreferencesUpdated : LiveData<Unit> = _userPreferencesUpdated

    init {
        authRepository.firebaseUserLiveData.observeForever {
            reloadUserPreferences()
            _userPreferencesUpdated.postValue(Unit)
        }
    }

    var favoriteRoutes = getFavoriteRoutesPreference()
    val loadFromCache = PreferenceLong(
        key = PREF_LOAD_FROM_CACHE,
        store = getBaseStore(),
        def = 0L
    )

    private fun reloadUserPreferences() {
        favoriteRoutes = getFavoriteRoutesPreference()
    }

    private fun getFavoriteRoutesPreference(): PreferenceStringSet {
        return PreferenceStringSet(
            key = PREF_ROUTES_LIST + authRepository.uid,
            store = getBaseStore())
    }

    private fun getBaseStore(): SharedPreferences = app.getSharedPreferences(config.appId, MODE_PRIVATE)

    fun saveFavoriteRoutes(routes: Set<String>) {
        favoriteRoutes.value = routes
    }

    fun isLoadFromCache(): Boolean {
        return loadFromCache.value + CACHE_DELTA > System.currentTimeMillis()
    }

    fun updateTimeLoadFromCache(){
        loadFromCache.value = System.currentTimeMillis()
    }

    companion object{
        const val PREF_ROUTES_LIST = "pref_routes_list_"
        const val PREF_LOAD_FROM_CACHE = "pref_load_from_cache"

        val CACHE_DELTA = TimeUnit.DAYS.toMillis(1)
    }
}
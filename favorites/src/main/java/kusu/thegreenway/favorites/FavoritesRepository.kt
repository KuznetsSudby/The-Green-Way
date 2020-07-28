package kusu.thegreenway.favorites

import kusu.thegreenway.preferences.PreferencesRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoritesRepository @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {

    private var favoriteRoutes: Set<String> = hashSetOf()

    init {
        preferencesRepository.userPreferencesUpdated.observeForever {
            favoriteRoutes = preferencesRepository.favoriteRoutes.value
        }
    }

    fun isContainsRoute(id: String) = favoriteRoutes.contains(id)

    private fun addRoute(id: String) {
        favoriteRoutes = favoriteRoutes + id
        preferencesRepository.saveFavoriteRoutes(favoriteRoutes)
    }

    private fun removeRoute(id: String) {
        favoriteRoutes = favoriteRoutes - id
        preferencesRepository.saveFavoriteRoutes(favoriteRoutes)
    }

    fun changeRoute(id: String) {
        if (isContainsRoute(id))
            removeRoute(id)
        else
            addRoute(id)
    }
}
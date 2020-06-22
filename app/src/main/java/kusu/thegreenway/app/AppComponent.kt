package kusu.thegreenway.app

import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import kusu.thegreenway.database.firestore.FirestoreBinds
import kusu.thegreenway.ui.main.MainModule
import kusu.thegreenway.ui.main.account.AccountModule
import kusu.thegreenway.ui.main.details.dot.DotDetailsModule
import kusu.thegreenway.ui.main.details.dot.DotDetailsViewModel
import kusu.thegreenway.ui.main.details.route.RouteDetailsModule
import kusu.thegreenway.ui.main.favorites.FavoritesModule
import kusu.thegreenway.ui.main.map.MapModule

import javax.inject.Singleton


@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        AppModule::class,
        FirestoreBinds::class,
        MainModule::class,
        MapModule::class,
        DotDetailsModule::class,
        RouteDetailsModule::class,
        AccountModule::class,
        FavoritesModule::class
    ]
)
interface AppComponent : AndroidInjector<App> {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: App): AppComponent
    }
}
package kusu.thegreenway.app

import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import kusu.thegreenway.account.ui.AccountModule
import kusu.thegreenway.database.firestore.FirestoreBinds
import kusu.thegreenway.ui.details.dot.DotDetailsModule
import kusu.thegreenway.ui.details.route.RouteDetails
import kusu.thegreenway.ui.details.route.RouteDetailsModule
import kusu.thegreenway.ui.favorites.FavoritesModule
import kusu.thegreenway.ui.main.MainModule
import kusu.thegreenway.ui.map.MapModule
import kusu.thegreenway.ui.routes.RoutesModule

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
        FavoritesModule::class,
        RoutesModule::class
    ]
)
interface AppComponent : AndroidInjector<App> {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: App): AppComponent
    }
}
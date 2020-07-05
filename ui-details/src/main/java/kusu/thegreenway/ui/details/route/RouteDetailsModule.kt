package kusu.thegreenway.ui.details.route

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import kusu.thegreenway.common.dagger.ViewModelBuilder
import kusu.thegreenway.common.dagger.ViewModelKey

@Module
abstract class RouteDetailsModule {

    @ContributesAndroidInjector(modules = [
        ViewModelBuilder::class
    ])
    internal abstract fun routeDetailsFragment(): kusu.thegreenway.ui.details.route.RouteDetails

    @Binds
    @IntoMap
    @ViewModelKey(kusu.thegreenway.ui.details.route.RouteDetailsViewModel::class)
    abstract fun bindViewModel(viewmodel: kusu.thegreenway.ui.details.route.RouteDetailsViewModel): ViewModel
}

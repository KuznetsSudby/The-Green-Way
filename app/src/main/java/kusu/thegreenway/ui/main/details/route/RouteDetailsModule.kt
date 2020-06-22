package kusu.thegreenway.ui.main.details.route

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import kusu.thegreenway.dagger.ViewModelBuilder
import kusu.thegreenway.dagger.ViewModelKey

@Module
abstract class RouteDetailsModule {

    @ContributesAndroidInjector(modules = [
        ViewModelBuilder::class
    ])
    internal abstract fun routeDetailsFragment(): RouteDetails

    @Binds
    @IntoMap
    @ViewModelKey(RouteDetailsViewModel::class)
    abstract fun bindViewModel(viewmodel: RouteDetailsViewModel): ViewModel
}

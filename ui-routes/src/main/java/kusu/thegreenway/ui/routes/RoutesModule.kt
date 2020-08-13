package kusu.thegreenway.ui.routes

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import kusu.thegreenway.common.dagger.ViewModelBuilder
import kusu.thegreenway.common.dagger.ViewModelKey

@Module
abstract class RoutesModule {

    @ContributesAndroidInjector(modules = [
        ViewModelBuilder::class
    ])
    internal abstract fun routesFragment(): RoutesFragment

    @Binds
    @IntoMap
    @ViewModelKey(RoutesViewModel::class)
    abstract fun bindViewModel(viewmodel: RoutesViewModel): ViewModel
}

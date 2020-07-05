package kusu.thegreenway.ui.map

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import kusu.thegreenway.common.dagger.ViewModelBuilder
import kusu.thegreenway.common.dagger.ViewModelKey

@Module
abstract class MapModule {

    @ContributesAndroidInjector(modules = [
        ViewModelBuilder::class
    ])
    internal abstract fun mapFragment(): MapFragment

    @Binds
    @IntoMap
    @ViewModelKey(kusu.thegreenway.ui.map.MapViewModel::class)
    abstract fun bindViewModel(viewmodel: kusu.thegreenway.ui.map.MapViewModel): ViewModel
}

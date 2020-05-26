package kusu.thegreenway.ui.main.map

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import kusu.thegreenway.dagger.ViewModelBuilder
import kusu.thegreenway.dagger.ViewModelKey

@Module
abstract class MapModule {

    @ContributesAndroidInjector(modules = [
        ViewModelBuilder::class
    ])
    internal abstract fun mapFragment(): MapFragment

    @Binds
    @IntoMap
    @ViewModelKey(MapViewModel::class)
    abstract fun bindViewModel(viewmodel: MapViewModel): ViewModel
}

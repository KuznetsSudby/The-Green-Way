package kusu.thegreenway.ui.main.favorites

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import kusu.thegreenway.dagger.ViewModelBuilder
import kusu.thegreenway.dagger.ViewModelKey

@Module
abstract class FavoritesModule {

    @ContributesAndroidInjector(modules = [
        ViewModelBuilder::class
    ])
    internal abstract fun favoritesFragment(): FavoritesFragment

    @Binds
    @IntoMap
    @ViewModelKey(FavoritesViewModel::class)
    abstract fun bindViewModel(viewmodel: FavoritesViewModel): ViewModel
}

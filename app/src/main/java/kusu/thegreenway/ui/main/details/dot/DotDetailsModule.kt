package kusu.thegreenway.ui.main.details.dot

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import kusu.thegreenway.dagger.ViewModelBuilder
import kusu.thegreenway.dagger.ViewModelKey

@Module
abstract class DotDetailsModule {

    @ContributesAndroidInjector(modules = [
        ViewModelBuilder::class
    ])
    internal abstract fun dotDetailsFragment(): DotDetails

    @Binds
    @IntoMap
    @ViewModelKey(DotDetailsViewModel::class)
    abstract fun bindViewModel(viewmodel: DotDetailsViewModel): ViewModel
}

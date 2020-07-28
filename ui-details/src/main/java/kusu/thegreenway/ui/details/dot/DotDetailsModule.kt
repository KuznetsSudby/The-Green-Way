package kusu.thegreenway.ui.details.dot

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import kusu.thegreenway.common.dagger.ViewModelBuilder
import kusu.thegreenway.common.dagger.ViewModelKey

@Module
abstract class DotDetailsModule {

    @ContributesAndroidInjector(modules = [
        ViewModelBuilder::class
    ])
    internal abstract fun dotDetailsFragment(): DotDetails

    @Binds
    @IntoMap
    @ViewModelKey(kusu.thegreenway.ui.details.dot.DotDetailsViewModel::class)
    abstract fun bindViewModel(viewmodel: kusu.thegreenway.ui.details.dot.DotDetailsViewModel): ViewModel
}

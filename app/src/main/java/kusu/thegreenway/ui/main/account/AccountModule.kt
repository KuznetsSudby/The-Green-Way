package kusu.thegreenway.ui.main.account

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import kusu.thegreenway.dagger.ViewModelBuilder
import kusu.thegreenway.dagger.ViewModelKey

@Module
abstract class AccountModule {

    @ContributesAndroidInjector(modules = [
        ViewModelBuilder::class
    ])
    internal abstract fun accountFragment(): AccountFragment

    @Binds
    @IntoMap
    @ViewModelKey(AccountViewModel::class)
    abstract fun bindViewModel(viewmodel: AccountViewModel): ViewModel
}

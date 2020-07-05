package kusu.thegreenway.ui.main

import dagger.Module
import dagger.android.ContributesAndroidInjector
import kusu.thegreenway.common.dagger.ViewModelBuilder

@Module
abstract class MainModule {

    @ContributesAndroidInjector(modules = [
        ViewModelBuilder::class
    ])
    internal abstract fun mainActivity(): MainActivity
}

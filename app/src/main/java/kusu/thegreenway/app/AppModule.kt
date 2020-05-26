package kusu.thegreenway.app

import android.content.Context
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Dispatchers
import kusu.thegreenway.app.App
import javax.inject.Singleton

@Module
class AppModule {

    @Singleton
    @Provides
    fun provideIoDispatcher() = Dispatchers.IO

    @Provides
    fun provideContext(application: App): Context {
        return application.applicationContext
    }
}
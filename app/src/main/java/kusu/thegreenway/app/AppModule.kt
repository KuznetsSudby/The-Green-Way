package kusu.thegreenway.app

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Dispatchers
import kusu.thegreenway.BuildConfig
import kusu.thegreenway.common.dagger.Config
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

    @Provides
    fun provideApplication(application: App): Application {
        return application
    }

    @Provides
    fun providesConfig(): Config {
        return Config(
            versionName = BuildConfig.VERSION_NAME,
            appId = BuildConfig.APPLICATION_ID
        )
    }
}
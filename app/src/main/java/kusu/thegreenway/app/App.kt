package kusu.thegreenway.app

import android.app.Application
import com.yandex.mapkit.MapKitFactory
import dagger.Module
import dagger.Provides
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

@Module
class App : DaggerApplication() {

    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey("e92d220b-09ec-4960-bb42-a06d6b8b716f")
        MapKitFactory.initialize(this)
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.factory().create(this);
    }


    @Provides
    fun provideApplication(): App = this
}
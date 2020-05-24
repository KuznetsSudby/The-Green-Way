package kusu.thegreenway

import android.app.Application
import com.yandex.mapkit.MapKitFactory

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        MapKitFactory.setApiKey("e92d220b-09ec-4960-bb42-a06d6b8b716f")
    }
}
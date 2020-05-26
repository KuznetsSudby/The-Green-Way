package kusu.thegreenway.ui.main

import android.os.Bundle
import com.yandex.mapkit.MapKitFactory
import dagger.android.support.DaggerAppCompatActivity
import kusu.thegreenway.R
import kusu.thegreenway.ui.main.map.MapFragment

class MainActivity : DaggerAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        MapKitFactory.initialize(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, MapFragment())
                    .commitNow()
        }
    }


    override fun onStop() { // Activity onStop call must be passed to both MapView and MapKit instance.
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onStart() { // Activity onStart call must be passed to both MapView and MapKit instance.
        super.onStart()
        MapKitFactory.getInstance().onStart()
    }
}

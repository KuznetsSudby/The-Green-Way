package kusu.thegreenway.ui.main

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.LabelVisibilityMode.LABEL_VISIBILITY_SELECTED
import com.yandex.mapkit.MapKitFactory
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.a_main.*
import kusu.thegreenway.R
import kusu.thegreenway.auth.AuthRepository
import kusu.thegreenway.common.OnBackPressable
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {

    private lateinit var navController: NavController

    @Inject
    lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.a_main)

        navController = findNavController(R.id.nav_host_fragment)
        bottomNavigation.setupWithNavController(navController)
        bottomNavigation.labelVisibilityMode = LABEL_VISIBILITY_SELECTED

        authRepository.isAuth.observe(this, Observer {

        })
    }

    override fun onBackPressed() {
        val fragment = (supportFragmentManager.fragments.getOrNull(0) as NavHostFragment).childFragmentManager.fragments.getOrNull(0)
        if (fragment is OnBackPressable && fragment.onBackPressed())
            return
        super.onBackPressed()
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

package kusu.thegreenway.ui.main

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.type.LatLng
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.map.*
import com.yandex.runtime.image.ImageProvider
import kotlinx.android.synthetic.main.main_fragment.*
import kusu.thegreenway.R
import kusu.thegreenway.database.models.Dot
import kusu.thegreenway.database.models.DotType
import kusu.thegreenway.database.models.Route
import kusu.thegreenway.utils.EventObserver
import kusu.thegreenway.utils.messageOr
import kusu.thegreenway.utils.observeVisibility
import kusu.thegreenway.utils.toast

class MainFragment : Fragment(), MapObjectTapListener {

    private lateinit var viewModel: MainViewModel

    val mapPoints: MutableList<MapObject> = ArrayList()
    var selectedRoute: PolylineMapObject? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return MainViewModel() as T
            }
        })[MainViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView.map.move(
            CameraPosition(TARGET_LOCATION, 8.0f, 0.0f, 0.0f),
            Animation(Animation.Type.LINEAR, 0f),
            null
        )
        mapView.map.move(
            CameraPosition(TARGET_LOCATION, 11.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 3f),
            null
        )

        progressContainer.observeVisibility(viewLifecycleOwner, viewModel.dbLoading)

        viewModel.dbException.observe(viewLifecycleOwner, EventObserver {
            it.messageOr("Error").toast(requireContext())
        })

        viewModel.routes.observe(viewLifecycleOwner, Observer { routes ->
            reinitializeMapRoutes(routes)
        })

        viewModel.selectedItem.observe(viewLifecycleOwner, Observer {
            it?.dots?.find { it.type?.id == DotType.ROUTE_START }?.let { dot ->
                mapView.map.move(
                    CameraPosition(dot.position.toPoint(), 14.0f, 0.0f, 0.0f),
                    Animation(Animation.Type.SMOOTH, 1.5f),
                    null
                )
            }
        })
    }

    private fun reinitializeMapRoutes(routes: List<Route>) {
        clearMap()

        val previousSelectedId = viewModel.selectedItem.value?.id
        var isFound = false

        routes.forEach { route ->
            mapView.map.mapObjects.addPolyline(
                Polyline(route.lines.map { it.toPoint() })
            ).also { polyline ->
                polyline.userData = route
                polyline.addTapListener(this@MainFragment)
                polyline.unselect(resources)
                if (route.id == previousSelectedId) {
                    isFound = true
                    selectRoute(polyline)
                }
            }
        }

        if (isFound.not())
            selectRoute(null)
    }

    private fun selectRoute(polyline: PolylineMapObject?) {
        viewModel.selectRoute(polyline?.userData as Route?)
        selectedRoute = polyline
        selectedRoute?.let { route ->
            route.select(resources)
            addDots(route.userData as Route)
        }
    }

    private fun addDots(route: Route) {
        route.dots.forEach { dot ->
            mapView.map.mapObjects.addPlacemark(
                dot.position.toPoint(),
                ImageProvider.fromResource(requireContext(), dot.type.convertToIcon())
            ).also { dotObject ->
                dotObject.userData = dot
                dotObject.addTapListener(this@MainFragment)
                mapPoints.add(dotObject)
            }
        }
    }

    private fun clearDots() {
        mapPoints.forEach { dot ->
            mapView.map.mapObjects.remove(dot)
        }
        mapPoints.clear()
    }

    private fun clearMap() {
        mapView.map.mapObjects.clear()
        selectedRoute = null
        mapPoints.clear()
    }


    private fun selectOnMap(mapObject: MapObject) {
        when (mapObject) {
            is PolylineMapObject -> {
                selectedRoute.unselect(resources)
                clearDots()
                if (selectedRoute == mapObject)
                    selectRoute(null)
                else
                    selectRoute(mapObject)
            }
            is PlacemarkMapObject -> {
                toast(requireContext(), (mapObject.userData as Dot).description)
            }
        }
    }

    override fun onStop() {
        mapView.onStop()
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    companion object {
        private val TARGET_LOCATION = Point(53.899916, 27.558904)
    }

    override fun onMapObjectTap(mapObject: MapObject, p1: Point): Boolean {
        selectOnMap(mapObject)
        return true
    }
}


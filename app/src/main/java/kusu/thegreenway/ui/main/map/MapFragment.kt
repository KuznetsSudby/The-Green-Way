package kusu.thegreenway.ui.main.map

import android.os.Bundle
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.logo.Alignment
import com.yandex.mapkit.logo.HorizontalAlignment
import com.yandex.mapkit.logo.VerticalAlignment
import com.yandex.mapkit.map.*
import com.yandex.runtime.image.ImageProvider
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.main_fragment.*
import kusu.thegreenway.R
import kusu.thegreenway.database.models.Dot
import kusu.thegreenway.database.models.DotType
import kusu.thegreenway.database.models.Route
import kusu.thegreenway.utils.EventObserver
import kusu.thegreenway.utils.messageOr
import kusu.thegreenway.utils.observeVisibility
import kusu.thegreenway.utils.toast
import javax.inject.Inject

class MapFragment : DaggerFragment(), MapObjectTapListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<MapViewModel> { viewModelFactory }

    val mapPoints: MutableList<MapObject> = ArrayList()

    var selectedRoute: PolylineMapObject? = null
    var selectedDot: PlacemarkMapObject? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //TODO save previous camera position
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
        mapView.map.logo.setAlignment(Alignment(HorizontalAlignment.RIGHT, VerticalAlignment.TOP))

        progressContainer.observeVisibility(viewLifecycleOwner, viewModel.dbLoading)

        viewModel.dbException.observe(viewLifecycleOwner, EventObserver {
            it.messageOr("Error").toast(requireContext())
        })

        viewModel.routes.observe(viewLifecycleOwner, Observer { routes ->
            reinitializeMapRoutes(routes)
        })

        viewModel.selectedItem.observe(viewLifecycleOwner, Observer { route ->
            route?.dots?.find { it.type.id == DotType.ROUTE_START }?.let { dot ->
                mapView.map.move(
                    CameraPosition(dot.position.toPoint(), 14.0f, 0.0f, 0.0f),
                    Animation(Animation.Type.SMOOTH, 1.5f),
                    null
                )
            }
        })
        viewModel.showDescription.observe(viewLifecycleOwner, Observer { show ->
            textLabel.text = viewModel.getDescriptionText()

//            val constraint = ConstraintSet()
//            constraint.clone(main)
//
//            if (show) {
//                constraint.connect(R.id.descriptionContainer, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
//                constraint.clear(R.id.descriptionContainer, ConstraintSet.TOP)
//            }else{
//                constraint.connect(R.id.descriptionContainer, ConstraintSet.TOP, R.id.mapView, ConstraintSet.BOTTOM)
//                constraint.clear(R.id.descriptionContainer, ConstraintSet.BOTTOM)
//            }
//
//            TransitionManager.beginDelayedTransition(main)
//            constraint.applyTo(main)
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
                polyline.addTapListener(this@MapFragment)
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
        selectedRoute.unselect(resources)
        selectDot(null)
        //TODO need clear optimization. Remove / add only dots difference. Also add animation for this
        clearDots()
        viewModel.selectRoute(polyline?.userData as Route?)
        selectedRoute = polyline
        selectedRoute?.let { route ->
            route.select(resources)
            addDots((route.userData as Route).dots)
        } ?: run {
            addDots(viewModel.dots.value?.filter { it.isRouteSpecific.not() } ?: emptyList())
        }
    }

    private fun selectDot(placemarkMapObject: PlacemarkMapObject?) {
        selectedDot.unselect(resources)
        selectedDot = placemarkMapObject
        viewModel.selectDot(placemarkMapObject?.userData as Dot?)
        selectedDot.select(resources)
    }


    private fun addDots(dots: List<Dot>) {
        dots.forEach { dot ->
            mapView.map.mapObjects.addPlacemark(
                dot.position.toPoint(),
                ImageProvider.fromResource(
                    requireContext(),
                    dot.type.convertToIcon()
                ),
                getBaseIconStyle()
            ).also { dotObject ->
                dotObject.userData = dot
                dotObject.addTapListener(this@MapFragment)
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
                if (selectedRoute == mapObject)
                    selectRoute(null)
                else
                    selectRoute(mapObject)
            }
            is PlacemarkMapObject -> {
                if (selectedDot == mapObject)
                    selectDot(null)
                else
                    selectDot(mapObject)
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


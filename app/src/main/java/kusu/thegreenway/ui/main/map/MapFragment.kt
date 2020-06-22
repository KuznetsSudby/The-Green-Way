package kusu.thegreenway.ui.main.map

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.logo.Alignment
import com.yandex.mapkit.logo.HorizontalAlignment
import com.yandex.mapkit.logo.VerticalAlignment
import com.yandex.mapkit.map.*
import com.yandex.runtime.image.ImageProvider
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.f_map.*
import kusu.thegreenway.R
import kusu.thegreenway.database.models.Dot
import kusu.thegreenway.database.models.DotType
import kusu.thegreenway.database.models.Route
import kusu.thegreenway.ui.main.favorites.common.DetailsHolder
import kusu.thegreenway.utils.*
import javax.inject.Inject

class MapFragment : DaggerFragment(), MapObjectTapListener, OnBackPressable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by activityViewModels<MapViewModel> { viewModelFactory }

    val mapDotsMap = HashMap<String, PlacemarkMapObject>()

    var selectedRoute: PolylineMapObject? = null
    var selectedDot: PlacemarkMapObject? = null

    lateinit var detailsHolder: DetailsHolder

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.f_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView.map.logo.setAlignment(Alignment(HorizontalAlignment.RIGHT, VerticalAlignment.TOP))
        progressContainer.observeVisibility(viewLifecycleOwner, viewModel.dbLoading)

        detailsHolder = DetailsHolder(descriptionShort)
        detailsButton.setOnClickListener { openDetails(viewModel.getSelectedObject()) }

        viewModel.dbException.observe(viewLifecycleOwner, EventObserver {
            it.messageOr("Error").toast(requireContext())
        })

        viewModel.routes.observe(viewLifecycleOwner, Observer { routes ->
            reinitializeMapRoutes(routes)
        })

        viewModel.selectedItem.observe(viewLifecycleOwner, Observer { route ->
            route?.dots?.find { it.type.id == DotType.ROUTE_START }?.let { dot ->
                mapView.map.move(
                    CameraPosition(dot.position.toPoint(), 14.0f, 0.0f, 0.0f).savePosition(viewModel),
                    Animation(Animation.Type.SMOOTH, 1.5f),
                    null
                )
            }
        })
        viewModel.showDescription.observe(viewLifecycleOwner, Observer { show ->
            viewModel.getSelectedObject()?.let {
                detailsHolder.bind(it, viewModel.favoritesModel)
            }
            animateDescriptionContainer(show, selectedRoute == null)
        })
        viewModel.favoritesModel.message.observe(viewLifecycleOwner, EventObserver {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        })
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0)
        }
    }

    private fun openDetails(selectedObject: Any?) {
        if (selectedObject == null)
            return
        when (selectedObject) {
            is Dot -> {
                findNavController().navigate(
                    R.id.action_mapFragment_to_dotDetailsFragment, bundleOf(
                        DETAILS to selectedObject
                    )
                )
            }
            is Route -> {
                findNavController().navigate(
                    R.id.action_mapFragment_to_routeDetailsFragment, bundleOf(
                        DETAILS to selectedObject
                    )
                )
            }
        }
    }

    private fun moveCamera() {
        viewModel.cameraPosition.value?.let { position ->
            mapView.map.move(
                position,
                Animation(Animation.Type.LINEAR, 0f),
                null
            )
        } ?: run {
            mapView.map.move(
                CameraPosition(TARGET_LOCATION, 8.5f, 0.0f, 0.0f),
                Animation(Animation.Type.LINEAR, 0f),
                null
            )
            mapView.map.move(
                CameraPosition(TARGET_LOCATION, 11.0f, 0.0f, 0.0f).savePosition(viewModel),
                Animation(Animation.Type.SMOOTH, 1.5f),
                null
            )
        }

        mapView.map.addCameraListener { map, cameraPosition, cameraUpdateSource, b ->
            viewModel.savePosition(cameraPosition)
        }

    }

    private fun animateDescriptionContainer(show: Boolean, isDot: Boolean) {
        val constraint = ConstraintSet()
        constraint.clone(main)

        constraint.setVisibility(R.id.routeGroup, if (isDot) GONE else VISIBLE)
        if (show) {
            constraint.connect(R.id.descriptionContainer, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
            constraint.clear(R.id.descriptionContainer, ConstraintSet.TOP)
        } else {
            constraint.connect(R.id.descriptionContainer, ConstraintSet.TOP, R.id.mapView, ConstraintSet.BOTTOM)
            constraint.clear(R.id.descriptionContainer, ConstraintSet.BOTTOM)
        }

        TransitionManager.beginDelayedTransition(main)
        constraint.applyTo(main)
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
                polyline.unselect(resources, viewModel.favoritesModel.toColor(route))
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
        selectedRoute?.let {
            it.unselect(resources, viewModel.favoritesModel.toColor(it.userData as Route))
        }
        selectDot(null)
        viewModel.selectRoute(polyline?.userData as Route?)
        selectedRoute = polyline
        selectedRoute?.let { route ->
            route.select(resources, viewModel.favoritesModel.toColor(route.userData as Route))
            setDots((route.userData as Route).dots)
        } ?: run {
            setDots(viewModel.dots.value?.filter { it.isRouteSpecific.not() } ?: emptyList())
        }
    }

    private fun selectDot(placemarkMapObject: PlacemarkMapObject?) {
        selectedDot.unselect()
        selectedDot = placemarkMapObject
        viewModel.selectDot(placemarkMapObject?.userData as Dot?)
        selectedDot.select()
    }


    private fun setDots(dots: List<Dot>) {
        val addDots = HashMap<String, PlacemarkMapObject>()
        val removeDots = mapDotsMap.keys.map { it }.toMutableSet()

        dots.forEach { dot ->
            if (removeDots.contains(dot.id)){
                removeDots.remove(dot.id)
            }else{
                addDots[dot.id] = mapView.map.mapObjects.addPlacemark(
                    dot.position.toPoint(),
                    ImageProvider.fromResource(
                        requireContext(),
                        dot.type.convertToIcon()
                    ),
                    getBaseIconStyle()
                ).apply {
                    userData = dot
                    addTapListener(this@MapFragment)
                    addAnimation()
                }
            }
        }

        mapDotsMap.putAll(addDots)
        removeDots.forEach { key ->
            mapDotsMap.remove(key)?.removeAnimation(mapView.map.mapObjects)
        }
    }

    private fun clearMap() {
        mapView.map.mapObjects.clear()
        selectedRoute = null
        mapDotsMap.clear()
    }


    private fun selectOnMap(mapObject: MapObject) {
        when (mapObject) {
            is PolylineMapObject -> {
                if (selectedRoute == mapObject) {
                    if (selectedDot != null)
                        selectDot(null)
                    else
                        selectRoute(null)
                } else
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
        moveCamera()
    }

    override fun onBackPressed(): Boolean {
        if (selectedDot != null) {
            selectDot(null)
            return true
        } else if (selectedRoute != null) {
            selectRoute(null)
            return true
        }
        return false
    }

    override fun onMapObjectTap(mapObject: MapObject, p1: Point): Boolean {
        selectOnMap(mapObject)
        return true
    }

    companion object {

        private val TARGET_LOCATION = Point(53.899916, 27.558904)

        const val DETAILS = "details"
    }
}

private fun CameraPosition.savePosition(viewModel: MapViewModel): CameraPosition {
    viewModel.savePosition(this)
    return this
}


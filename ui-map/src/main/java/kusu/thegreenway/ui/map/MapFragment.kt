package kusu.thegreenway.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.PointF
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.transition.TransitionManager
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.logo.Alignment
import com.yandex.mapkit.logo.HorizontalAlignment
import com.yandex.mapkit.logo.VerticalAlignment
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.internal.CompositeIconBinding
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.image.ImageProvider
import com.yandex.runtime.image.ImageProvider.fromResource
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.f_map.*
import kusu.thegreenway.common.*
import kusu.thegreenway.common.map.*
import kusu.thegreenway.common.models.Dot
import kusu.thegreenway.common.models.DotType
import kusu.thegreenway.common.models.Route
import javax.inject.Inject


class MapFragment : DaggerFragment(R.layout.f_map), MapObjectTapListener, OnBackPressable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by activityViewModels<MapViewModel> { viewModelFactory }

    val mapDotsMap = HashMap<String, PlacemarkMapObject>()

    var selectedRoute: PolylineMapObject? = null
    var selectedDot: PlacemarkMapObject? = null

    var user : PlacemarkMapObject? = null

    val args: MapFragmentArgs by navArgs()

    var startPosition = TARGET_LOCATION

    lateinit var detailsHolder: DetailsHolder

    val fusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireContext())
    }

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult ?: return
            locationResult.lastLocation.let {
                moveUserIcon(Point(it.latitude, it.longitude))
            }
        }
    }

    private fun moveUserIcon(point: Point) {
        user?.let{
            it.moveTo(point)
        } ?: run {
            user = mapView.map.mapObjects.addPlacemark(
                point,
                fromResource(requireContext(), R.drawable.ic_pin_user)
            )
        }
    }

    val locationRequest = LocationRequest.create().apply {
        interval = 10_000
        fastestInterval = 3_000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.initialize(args)
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
            (route?.dots?.find { it.type.id == DotType.ROUTE_START }?.position ?: route?.lines?.getOrNull(0))?.let { dot ->
                mapView.map.move(
                    CameraPosition(dot.toPoint(), 14.0f, 0.0f, 0.0f).savePosition(viewModel),
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
            it.toast(requireContext())
        })
    }

    @SuppressLint("MissingPermission")
    private fun startListenLocation() {

        val locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val lastGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        val lastNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

        if (lastGps != null) {
            startPosition = Point(lastGps.latitude, lastGps.longitude)
        } else if (lastNetwork != null) {
            startPosition = Point(lastNetwork.latitude, lastNetwork.longitude)
        }

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun openDetails(selectedObject: Any?) {
        if (selectedObject == null)
            return
        when (selectedObject) {
            is Dot -> {
                findNavController().navigate(MapFragmentDirections.actionMapFragmentToDotDetailsFragment(selectedObject))
            }
            is Route -> {
                findNavController().navigate(MapFragmentDirections.actionMapFragmentToRouteDetailsFragment(selectedObject))
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
                CameraPosition(startPosition, 9f, 0.0f, 0.0f), //8.5f
                Animation(Animation.Type.LINEAR, 0f),
                null
            )
            mapView.map.move(
                CameraPosition(startPosition, 12.0f, 0.0f, 0.0f).savePosition(viewModel), //11f
                Animation(Animation.Type.SMOOTH, 1.5f),
                null
            )
        }

        mapView.map.addCameraListener { map, cameraPosition, cameraUpdateSource, b ->
            viewModel.savePosition(cameraPosition)
            compass.rotation = -cameraPosition.azimuth
            println("ExcM Save position")
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
            if (removeDots.contains(dot.id)) {
                removeDots.remove(dot.id)
            } else {
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
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    override fun onStart() {
        super.onStart()
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                0
            )
        } else {
            startListenLocation()
        }
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
    }
}

private fun CameraPosition.savePosition(viewModel: MapViewModel): CameraPosition {
    viewModel.savePosition(this)
    return this
}


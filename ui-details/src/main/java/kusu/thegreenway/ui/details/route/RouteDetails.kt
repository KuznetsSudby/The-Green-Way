package kusu.thegreenway.ui.details.route

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.chip.Chip
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.map.CameraPosition
import com.yandex.runtime.image.ImageProvider
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.f_route_details.*
import kusu.thegreenway.common.models.DotType
import kusu.thegreenway.common.models.Route
import kusu.thegreenway.common.models.TravelType
import kusu.thegreenway.common.EventObserver
import kusu.thegreenway.common.map.convertToIcon
import kusu.thegreenway.common.map.getBaseIconStyle
import kusu.thegreenway.common.map.select
import kusu.thegreenway.common.map.toPoint
import kusu.thegreenway.common.toast
import kusu.thegreenway.navigation.NavGraphDirections
import kusu.thegreenway.ui.details.R
import javax.inject.Inject


class RouteDetails : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by viewModels<RouteDetailsViewModel> { viewModelFactory }

    val args: RouteDetailsArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.f_route_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadDetails(args.details)

        viewModel.route.observe(viewLifecycleOwner, Observer { route ->
            routeTitle.text = route.title
            routeDescription.text = route.description

            viewPager.adapter = ImagesAdapter(route.images, ::openImage)
            indicator.attachTo(viewPager)
            indicator.reattach()

            chipGroup.removeAllViews()
            route.categories.forEach {
                val chip = Chip(context)
                chip.text = it.title
                chipGroup.addView(chip as View)
            }

            route.travelTypes.forEach {
                (when (it.id) {
                    TravelType.FOOT -> stepIcon
                    TravelType.BIKE -> bikeIcon
                    TravelType.HORSE -> horseIcon
                    TravelType.SKI -> skiIcon
                    TravelType.RAFTING -> raftingIcon
                    else -> null
                })?.visibility = VISIBLE
            }

            favoriteButton.setImageResource(viewModel.favoritesModel.toResource(route))
            favoriteButton.setOnClickListener {
                viewModel.favoritesModel.changeFavorite(route)
                favoriteButton.setImageResource(viewModel.favoritesModel.toResource(route))
            }

            difficultyLabel.text = route.difficulty.title
            timeLabel.text = getString(R.string.ui_time_value, route.minutes.toString())

            approvedContainer.serVisible(route.approved)
            childIcon.setImageResource(if (route.children) R.drawable.ic_check else R.drawable.ic_cross)
            disabilitiesIcon.setImageResource(if (route.disabilities) R.drawable.ic_check else R.drawable.ic_cross)
            animalIcon.setImageResource(if (route.animals) R.drawable.ic_check else R.drawable.ic_cross)

            mapView.map.mapObjects.addPolyline(
                Polyline(route.lines.map { it.toPoint() })
            ).select(resources, viewModel.favoritesModel.toColor(route))

            route.dots.find { it.type.id == DotType.ROUTE_START }?.let { dot ->
                mapView.map.move(
                    CameraPosition(dot.position.toPoint(), 13.0f, 0.0f, 0.0f),
                    Animation(Animation.Type.LINEAR, 0f),
                    null
                )
                mapView.map.move(
                    CameraPosition(dot.position.toPoint(), 15.0f, 0.0f, 0.0f),
                    Animation(Animation.Type.SMOOTH, 3f),
                    null
                )
            }

            route.dots.forEach { dot ->
                mapView.map.mapObjects.addPlacemark(
                    dot.position.toPoint(),
                    ImageProvider.fromResource(
                        requireContext(),
                        dot.type.convertToIcon()
                    ),
                    getBaseIconStyle()
                )
            }
        })

        viewModel.favoritesModel.message.observe(viewLifecycleOwner, EventObserver {
            it.toast(requireContext())
        })

        transparentImage.setOnTouchListener(object : View.OnTouchListener {

            override fun onTouch(p0: View?, event: MotionEvent?): Boolean {
                val action = event?.getAction()
                return when (action) {
                    MotionEvent.ACTION_DOWN -> {
                        // Disallow ScrollView to intercept touch events.
                        scrollView.requestDisallowInterceptTouchEvent(true)
                        // Disable touch on transparent view
                        false
                    }
                    MotionEvent.ACTION_UP -> {
                        // Allow ScrollView to intercept touch events.
                        scrollView.requestDisallowInterceptTouchEvent(false)
                        true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        scrollView.requestDisallowInterceptTouchEvent(true)
                        false
                    }
                    else -> true
                }
            }
        })

    }

    fun openImage(images: List<String>, position: Int){
        //Todo fix problem "viewPager2" + "TouchImageView"
//        val intent = Intent(requireContext(), Gallery::class.java)
//        intent.putExtra(Gallery.POSITION, position)
//        intent.putExtra(Gallery.IMAGES, images.toTypedArray())
//        requireActivity().startActivity(intent)
        findNavController().navigate(NavGraphDirections.actionOpenImage(images[position]))
    }

    override fun onStop() {
        mapView.onStop()
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }
}

private fun View.serVisible(approved: Boolean) {
    this.visibility = if (approved) VISIBLE else GONE
}

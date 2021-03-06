package kusu.thegreenway.ui.details.dot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.yandex.mapkit.Animation
import com.yandex.mapkit.map.CameraPosition
import com.yandex.runtime.image.ImageProvider
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.f_dot_details.*
import kusu.thegreenway.common.map.convertToIcon
import kusu.thegreenway.common.map.getBaseIconStyle
import kusu.thegreenway.common.map.toPoint
import kusu.thegreenway.navigation.NavGraphDirections
import kusu.thegreenway.ui.details.R
import kusu.thegreenway.ui.details.route.ImagesAdapter
import javax.inject.Inject

class DotDetails : DaggerFragment(R.layout.f_dot_details) {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by viewModels<DotDetailsViewModel> { viewModelFactory }

    val args: DotDetailsArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadDetails(args.details)

        viewModel.dot.observe(viewLifecycleOwner, Observer { dot ->
            dotTitle.text = dot.title
            dotDescription.text = HtmlCompat.fromHtml(dot.description, HtmlCompat.FROM_HTML_MODE_LEGACY)
            dotType.text = dot.type.title

            viewPager.adapter = ImagesAdapter(dot.images, ::openImage)
            indicator.attachTo(viewPager)
            indicator.reattach()

            viewPager.visibility = if (dot.images.isEmpty()) View.GONE else View.VISIBLE
            discreteTopLine.visibility = if (dot.images.isEmpty()) View.GONE else View.VISIBLE
            indicator.visibility = if (dot.images.isEmpty()) View.GONE else View.VISIBLE

            mapView.map.move(
                CameraPosition(dot.position.toPoint(), 15.0f, 0.0f, 0.0f),
                Animation(Animation.Type.LINEAR, 0f),
                null
            )
            mapView.map.move(
                CameraPosition(dot.position.toPoint(), 17.0f, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, 3f),
                null
            )

            mapView.map.mapObjects.addPlacemark(
                dot.position.toPoint(),
                ImageProvider.fromBitmap(
                    convertToIcon(
                        requireContext(),
                        dot.type.category,
                        dot.type.id
                    )
                ),
                getBaseIconStyle()
            )
        })
    }

    fun openImage(images: List<String>, position: Int) {
        findNavController().navigate(NavGraphDirections.actionGallery(position.toLong(), images.toTypedArray()))
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

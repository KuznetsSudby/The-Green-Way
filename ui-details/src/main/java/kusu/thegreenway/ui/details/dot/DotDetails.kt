package kusu.thegreenway.ui.details.dot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.yandex.mapkit.Animation
import com.yandex.mapkit.map.CameraPosition
import com.yandex.runtime.image.ImageProvider
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.f_dot_details.*
import kotlinx.android.synthetic.main.f_dot_details.mapView
import kusu.thegreenway.common.map.convertToIcon
import kusu.thegreenway.common.map.getBaseIconStyle

import kusu.thegreenway.common.models.Dot
import kusu.thegreenway.common.map.toPoint
import kusu.thegreenway.ui.details.R
import javax.inject.Inject

class DotDetails : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by viewModels<DotDetailsViewModel> { viewModelFactory }

    val args: DotDetailsArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.f_dot_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadDetails(args.details)

        viewModel.dot.observe(viewLifecycleOwner, Observer { dot ->
            dotTitle.text = dot.title
            dotDescription.text = dot.description
            dotType.text = dot.type.title

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
                ImageProvider.fromResource(
                    requireContext(),
                    dot.type.convertToIcon()
                ),
                getBaseIconStyle()
            )
        })

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

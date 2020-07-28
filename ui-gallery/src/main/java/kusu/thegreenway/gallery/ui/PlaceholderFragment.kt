package kusu.thegreenway.gallery.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.i_gallery.*
import kusu.thegreenway.gallery.R

class PlaceholderFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.i_gallery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Picasso.get().load(requireArguments().getString(IMAGE)).into(imageView)
    }

    companion object {
        private const val IMAGE = "image"

        fun newInstance(image: String): PlaceholderFragment {
            val fragment = PlaceholderFragment()
            fragment.arguments = bundleOf(IMAGE to image)
            return fragment
        }
    }
}
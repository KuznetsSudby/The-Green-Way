package kusu.thegreenway.gallery.ui

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.navigation.navArgs
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.i_gallery.*
import kusu.thegreenway.gallery.R

/**
 * Created by KuSu on 25.09.2016.
 */
class Image : FragmentActivity() {

    val args: ImageArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.i_gallery)
        Picasso.get().load(args.image).into(imageView)
    }
}
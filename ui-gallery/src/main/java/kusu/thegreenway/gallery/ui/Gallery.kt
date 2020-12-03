package kusu.thegreenway.gallery.ui

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.navigation.navArgs
import kotlinx.android.synthetic.main.a_gallery.*
import kusu.thegreenway.gallery.R

class Gallery : FragmentActivity() {

    val args: GalleryArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.a_gallery)
        viewPager.adapter = GalleryPager(
            this,
            viewPager,
            args.images
        )
        indicator.attachTo(viewPager)
        indicator.reattach()
        viewPager.post{
            viewPager.currentItem = args.position.toInt()
        }
    }
}
package kusu.thegreenway.gallery.ui

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import kotlinx.android.synthetic.main.a_gallery.*
import kusu.thegreenway.gallery.R

class Gallery : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.a_gallery)
        viewPager.adapter = GalleryPager(
            this,
            intent.getStringArrayExtra(IMAGES) ?: arrayOf()
        )
        indicator.attachTo(viewPager)
        indicator.reattach()
        viewPager.post{
            viewPager.currentItem = intent.getIntExtra(POSITION, 0)
        }
    }

    companion object {

        const val IMAGES = "images"
        const val POSITION = "position"
    }
}
package kusu.thegreenway.gallery.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2

class GalleryPager(
    activity: FragmentActivity,
    val viewPager2: ViewPager2,
    val images: Array<String>
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = images.size

    override fun createFragment(position: Int): Fragment =
        PlaceholderFragment.newInstance(images[position]).setPager(viewPager2)
}

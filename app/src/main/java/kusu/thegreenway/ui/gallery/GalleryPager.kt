package kusu.thegreenway.ui.gallery

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class GalleryPager(
    activity: FragmentActivity,
    val images: Array<String>
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = images.size

    override fun createFragment(position: Int): Fragment = PlaceholderFragment.newInstance(images[position])
}

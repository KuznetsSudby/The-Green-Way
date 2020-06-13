package kusu.thegreenway.ui.image

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.i_gallery.*
import kusu.thegreenway.R

/**
 * Created by KuSu on 25.09.2016.
 */
class Image : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.i_gallery)
        Picasso.get().load(intent.getStringExtra(IMAGE)).into(imageView)
    }


    companion object {

        const val IMAGE = "image"
    }
}
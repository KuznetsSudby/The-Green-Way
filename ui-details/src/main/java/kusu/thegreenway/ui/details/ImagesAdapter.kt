package kusu.thegreenway.ui.details.route

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.i_image.view.*
import kusu.thegreenway.ui.details.R

class ImagesAdapter(
    val images: List<String>,
    val openImage: ((images: List<String>, position: Int) -> Unit)
) : RecyclerView.Adapter<ImageHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder =
        ImageHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.i_image, parent, false)
        )

    override fun getItemCount(): Int = images.size

    override fun onBindViewHolder(holder: ImageHolder, position: Int) = holder.itemView.run {
        Picasso.get().load(images[position]).into(holder.itemView.image)
        holder.itemView.image.setOnClickListener {
            openImage.invoke(images, position)
        }
    }
}

class ImageHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
package kusu.thegreenway.ui.favorites.common

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.i_details_short.view.*
import kusu.thegreenway.common.models.Dot
import kusu.thegreenway.common.models.Route
import kusu.thegreenway.favorites.FavoritesModel
import kusu.thegreenway.ui.favorites.R

class DetailsHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    fun bind(obj: Any, favoritesModel: FavoritesModel){
        itemView.routeGroup.visibility = View.GONE
        when (obj) {
            is Dot -> {
                itemView.textLabel.text = obj.title
                itemView.typeLabel.text = obj.type.title
            }
            is Route -> {
                itemView.routeGroup.visibility = View.VISIBLE
                itemView.textLabel.text = obj.title
                itemView.typeLabel.text = obj.difficulty.title
                itemView.timeLabel.text = itemView.context.resources.getString(R.string.ui_time_value, obj.minutes.toString())

                itemView.favoriteButton.setImageResource(favoritesModel.toResource(obj))
                itemView.favoriteButton.setOnClickListener {
                    favoritesModel.changeFavorite(obj)
                    itemView.favoriteButton.setImageResource(favoritesModel.toResource(obj))
                }

                itemView.chipGroup.removeAllViews()
                obj.categories.forEach {
                    val chip = Chip(itemView.context)
                    chip.text = it.title
                    itemView.chipGroup.addView(chip as View)
                }
            }
        }
    }
}
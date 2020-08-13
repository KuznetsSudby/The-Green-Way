package kusu.thegreenway.ui.favorites.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kusu.thegreenway.common.models.Route
import kusu.thegreenway.favorites.FavoritesModel
import kusu.thegreenway.ui.favorites.R

class DetailsAdapter(
    val items: MutableList<Route>,
    val favoritesModel: FavoritesModel,
    private val onRouteClick: (route: Route) -> Unit
) : RecyclerView.Adapter<DetailsHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailsHolder {
        return LayoutInflater.from(parent.context).inflate(R.layout.i_details_short, parent, false)
            .let {
                DetailsHolder(it)
            }
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: DetailsHolder, position: Int) {
        holder.bind(items[position], favoritesModel){
            items.remove(it)
            notifyItemRemoved(position)
        }
        holder.itemView.setOnClickListener {
            onRouteClick.invoke(items[position])
        }
    }
}
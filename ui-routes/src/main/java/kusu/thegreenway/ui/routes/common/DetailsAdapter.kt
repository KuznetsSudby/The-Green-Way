package kusu.thegreenway.ui.routes.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kusu.thegreenway.common.models.Route
import kusu.thegreenway.favorites.FavoritesModel
import kusu.thegreenway.ui.routes.R

class DetailsAdapter(
    val items: List<Route>,
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
        holder.bind(items[position], favoritesModel)
        holder.itemView.setOnClickListener {
            onRouteClick.invoke(items[position])
        }
    }
}
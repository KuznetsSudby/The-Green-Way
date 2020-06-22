package kusu.thegreenway.ui.main.favorites.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kusu.thegreenway.R
import kusu.thegreenway.database.models.Route
import kusu.thegreenway.ui.main.favorites.FavoritesModel

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
package kusu.thegreenway.ui.main.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.f_favorites.*

import kusu.thegreenway.R
import kusu.thegreenway.database.models.Route
import kusu.thegreenway.ui.main.favorites.common.DetailsAdapter
import kusu.thegreenway.ui.main.favorites.common.DetailsDecorator
import kusu.thegreenway.ui.main.map.MapFragment
import kusu.thegreenway.utils.EventObserver
import kusu.thegreenway.utils.messageOr
import kusu.thegreenway.utils.toast
import javax.inject.Inject

class FavoritesFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by viewModels<FavoritesViewModel> { viewModelFactory }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.f_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.favoritesModel.message.observe(viewLifecycleOwner, EventObserver {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        })

        viewModel.dbException.observe(viewLifecycleOwner, EventObserver {
            it.messageOr("Error").toast(requireContext())
        })

        list.layoutManager = LinearLayoutManager(requireContext())
        list.addItemDecoration(DetailsDecorator(requireContext()))

        viewModel.routes.observe(viewLifecycleOwner, Observer {
            list.adapter = DetailsAdapter(it, viewModel.favoritesModel, ::openRoute)
        })
    }

    private fun openRoute(route: Route) {
        findNavController().navigate(
            R.id.action_favoritesFragment_to_routeDetailsFragment, bundleOf(
                MapFragment.DETAILS to route
            )
        )
    }
}

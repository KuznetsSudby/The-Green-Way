package kusu.thegreenway.ui.routes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.f_routes.*

import kusu.thegreenway.common.EventObserver
import kusu.thegreenway.common.messageOr
import kusu.thegreenway.common.models.Route
import kusu.thegreenway.common.toast
import kusu.thegreenway.ui.routes.common.DetailsAdapter
import kusu.thegreenway.ui.routes.common.DetailsDecorator
import javax.inject.Inject

class RoutesFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by viewModels<RoutesViewModel> { viewModelFactory }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.f_routes, container, false)
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
        findNavController().navigate(RoutesFragmentDirections.actionRoutesFragmentToRouteDetailsFragment(route))
    }
}

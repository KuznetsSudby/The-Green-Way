package kusu.thegreenway.ui.routes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.f_routes.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce

import kusu.thegreenway.common.EventObserver
import kusu.thegreenway.common.asFlow
import kusu.thegreenway.common.messageOr
import kusu.thegreenway.common.models.Route
import kusu.thegreenway.common.toast
import kusu.thegreenway.ui.routes.common.DetailsAdapter
import kusu.thegreenway.ui.routes.common.DetailsDecorator
import javax.inject.Inject

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class RoutesFragment : DaggerFragment(R.layout.f_routes) {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by activityViewModels<RoutesViewModel> { viewModelFactory }

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

        viewModel.screenRoutes.observe(viewLifecycleOwner, Observer {
            list.adapter = DetailsAdapter(it, viewModel.favoritesModel, ::openRoute)
            updateFilterIcon()
        })

        lifecycleScope.launchWhenResumed {
            searchEdit.asFlow().debounce(300).collect {value ->
                viewModel.setSearch(value)
            }
        }

        viewModel.search.observe(viewLifecycleOwner, Observer {
            clear.visibility = if (it.isEmpty()) GONE else VISIBLE
            if (searchEdit.text.toString() != it){
                searchEdit.setText(it)
            }
        })

        clear.setOnClickListener {
            viewModel.clearSearch()
        }

        filter.setOnClickListener {
            findNavController().navigate(RoutesFragmentDirections.actionRoutesFragmentToFilter())
        }
    }

    private fun updateFilterIcon() {
        filter.setImageResource(
            if (viewModel.isFiltered())
                R.drawable.ic_filter_fill
            else
                R.drawable.ic_filter
        )
    }

    private fun openRoute(route: Route) {
        findNavController().navigate(RoutesFragmentDirections.actionRoutesFragmentToRouteDetailsFragment(route))
    }
}

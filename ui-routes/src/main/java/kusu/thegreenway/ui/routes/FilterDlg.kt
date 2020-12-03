package kusu.thegreenway.ui.routes

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.chip.Chip
import com.mohammedalaa.seekbar.DoubleValueSeekBarView
import com.mohammedalaa.seekbar.OnDoubleValueSeekBarChangeListener
import kotlinx.android.synthetic.main.d_filter.*
import kusu.thegreenway.common.views.FullScreenDaggerBottomSheetDialog
import javax.inject.Inject

class FilterDlg : FullScreenDaggerBottomSheetDialog(R.layout.d_filter) {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel by activityViewModels<RoutesViewModel> { viewModelFactory }

    lateinit var selectedRange: Pair<Int, Int>
    lateinit var selectedTypes: HashSet<String>
    lateinit var selectedCategories: HashSet<String>
    lateinit var selectedAvailabilityies: HashSet<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        selectedCategories = viewModel.categories.clone() as HashSet<String>
        selectedAvailabilityies = viewModel.availabilities.clone() as HashSet<String>
        selectedTypes = viewModel.types.clone() as HashSet<String>
        selectedRange = viewModel.range.copy()

        initFilters()

        closeButton.setOnClickListener {
            dismiss()
        }

        saveButton.setOnClickListener {
            viewModel.saveFilter(
                selectedTypes,
                selectedCategories,
                selectedAvailabilityies,
                selectedRange
            )
            dismiss()
        }

        clearButton.setOnClickListener {
            selectedTypes = RoutesViewModel.DEF_TYPES.clone() as HashSet<String>
            selectedCategories = RoutesViewModel.DEF_CATEGORIES.clone() as HashSet<String>
            selectedAvailabilityies = RoutesViewModel.DEF_AVAILABILITY.clone() as HashSet<String>
            selectedRange = RoutesViewModel.DEF_RANGE.copy()
            initFilters()
        }

        range.minValue = RoutesViewModel.DEF_RANGE.first
        range.maxValue = RoutesViewModel.DEF_RANGE.second

        range.currentMinValue = selectedRange.first
        range.currentMaxValue = selectedRange.second

        range.setOnRangeSeekBarViewChangeListener(object : OnDoubleValueSeekBarChangeListener{
            override fun onStartTrackingTouch(seekBar: DoubleValueSeekBarView?, min: Int, max: Int) {
            }

            override fun onStopTrackingTouch(seekBar: DoubleValueSeekBarView?, min: Int, max: Int) {
            }

            override fun onValueChanged(seekBar: DoubleValueSeekBarView?, min: Int, max: Int, fromUser: Boolean) {
                selectedRange = Pair(min, max)
                updateFilterIcon()
            }
        })
    }

    private fun initFilters() {
        initTypes()
        initCategories()
        initRange()
        initAvailability()
        updateFilterIcon()
    }

    private fun initRange() {

    }

    private fun updateFilterIcon() {
        logo.setImageResource(
            if (viewModel.isFiltered(
                    selectedTypes,
                    selectedCategories,
                    selectedAvailabilityies,
                    selectedRange
                )
            )
                R.drawable.ic_filter_fill
            else
                R.drawable.ic_filter
        )
    }

    private fun initTypes() {
        filterType.removeAllViews()
        val chipAll = layoutInflater.inflate(R.layout.el_chip_select, filterType, false) as Chip
        chipAll.text = getString(R.string.ui_all_types)
        chipAll.tag = null
        chipAll.isChecked = selectedTypes.isEmpty()
        chipAll.setOnClickListener { buttonView ->
            selectedTypes.clear()
            filterType.clearCheck()
            chipAll.isChecked = true
            updateFilterIcon()
        }
        filterType.addView(chipAll)

        viewModel.typesList.forEach {
            val chip = layoutInflater.inflate(R.layout.el_chip_select, filterType, false) as Chip
            chip.text = it.title
            chip.tag = it.id
            chip.isChecked = selectedTypes.contains(it.id)
            filterType.addView(chip)
            chip.setOnCheckedChangeListener { buttonView, isChecked ->
                buttonView.isChecked = isChecked
                if (isChecked) {
                    selectedTypes.add(buttonView.tag as String)
                    chipAll.isChecked = false
                } else {
                    selectedTypes.remove(buttonView.tag as String)
                    chipAll.isChecked = selectedTypes.isEmpty()
                }
                updateFilterIcon()
            }
        }
    }

    private fun initCategories() {
        filterCategory.removeAllViews()
        val chipAll = layoutInflater.inflate(R.layout.el_chip_select, filterCategory, false) as Chip
        chipAll.text = getString(R.string.ui_all_categories)
        chipAll.tag = null
        chipAll.isChecked = selectedCategories.isEmpty()
        chipAll.setOnClickListener { buttonView ->
            selectedCategories.clear()
            filterCategory.clearCheck()
            chipAll.isChecked = true
            updateFilterIcon()
        }
        filterCategory.addView(chipAll)

        viewModel.categoriesList.forEach {
            val chip = layoutInflater.inflate(R.layout.el_chip_select, filterCategory, false) as Chip
            chip.text = it.title
            chip.tag = it.id
            chip.isChecked = selectedCategories.contains(it.id)
            filterCategory.addView(chip)
            chip.setOnCheckedChangeListener { buttonView, isChecked ->
                buttonView.isChecked = isChecked
                if (isChecked) {
                    selectedCategories.add(buttonView.tag as String)
                    chipAll.isChecked = false
                } else {
                    selectedCategories.remove(buttonView.tag as String)
                    chipAll.isChecked = selectedCategories.isEmpty()
                }
                updateFilterIcon()
            }
        }
    }

    private fun initAvailability() {
        filterAvailability.removeAllViews()
        val chipAll = layoutInflater.inflate(R.layout.el_chip_select, filterAvailability, false) as Chip
        chipAll.text = getString(R.string.ui_all_routes)
        chipAll.tag = null
        chipAll.isChecked = selectedAvailabilityies.isEmpty()
        chipAll.setOnClickListener { buttonView ->
            selectedAvailabilityies.clear()
            filterAvailability.clearCheck()
            chipAll.isChecked = true
            updateFilterIcon()
        }
        filterAvailability.addView(chipAll)

        Availability.values().forEach {
            val chip = layoutInflater.inflate(R.layout.el_chip_select, filterAvailability, false) as Chip
            chip.text = getString(it.title)
            chip.tag = it.id
            chip.isChecked = selectedAvailabilityies.contains(it.id)
            filterAvailability.addView(chip)
            chip.setOnCheckedChangeListener { buttonView, isChecked ->
                buttonView.isChecked = isChecked
                if (isChecked) {
                    selectedAvailabilityies.add(buttonView.tag as String)
                    chipAll.isChecked = false
                } else {
                    selectedAvailabilityies.remove(buttonView.tag as String)
                    chipAll.isChecked = selectedAvailabilityies.isEmpty()
                }
                updateFilterIcon()
            }
        }
    }
}
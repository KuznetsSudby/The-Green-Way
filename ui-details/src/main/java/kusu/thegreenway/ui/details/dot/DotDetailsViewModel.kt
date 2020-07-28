package kusu.thegreenway.ui.details.dot

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kusu.thegreenway.common.models.Dot
import javax.inject.Inject

class DotDetailsViewModel @Inject constructor() : ViewModel() {

    private val _dot = MutableLiveData<Dot>()
    val dot: LiveData<Dot> = _dot

    fun loadDetails(dot: Dot) {
        _dot.postValue(dot)
    }
}

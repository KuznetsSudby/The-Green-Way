package kusu.thegreenway.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

class CombineLiveData<F, S, R>(first: LiveData<F>, second: LiveData<S>, combine: (F?, S?) -> R) :
    MediatorLiveData<R>() {

    init {
        addSource(first) {
            value = combine(it, second.value)
        }
        addSource(second) {
            value = combine(first.value, it)
        }
    }
}
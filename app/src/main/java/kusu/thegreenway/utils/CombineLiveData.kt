package by.fideware.alcoholtruck.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

class CombineLiveData<T>(first: LiveData<T>, second: LiveData<T>, combine: (T?, T?) -> T) :
    MediatorLiveData<T>() {

    init {
        addSource(first) {
            value = combine(it, second.value)
        }
        addSource(second) {
            value = combine(first.value, it)
        }
    }
}
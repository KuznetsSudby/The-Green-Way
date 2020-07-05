package kusu.thegreenway.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import kusu.thegreenway.common.Event
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor() {

    val firebaseUserLiveData = kusu.thegreenway.auth.FirebaseUserLiveData()

    val isAuth = Transformations.map(firebaseUserLiveData) { user ->
        user != null
    }

    val uid
        get() = firebaseUserLiveData.value?.uid

    val message = MutableLiveData<Event<String>>()
}
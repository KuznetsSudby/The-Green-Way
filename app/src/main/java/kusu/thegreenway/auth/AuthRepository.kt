package kusu.thegreenway.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import kusu.thegreenway.utils.Event
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor() {

    val firebaseUserLiveData = FirebaseUserLiveData()

    val isAuth = firebaseUserLiveData.map { user ->
        user != null
    }

    val uid
        get() = firebaseUserLiveData.value?.uid

    val message = MutableLiveData<Event<String>>()
}
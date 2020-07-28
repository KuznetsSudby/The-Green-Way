package kusu.thegreenway.account.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import kusu.thegreenway.common.Event
import javax.inject.Inject

class AccountViewModel @Inject constructor(
    private val authRepository: kusu.thegreenway.auth.AuthRepository
) : ViewModel() {

    val user: kusu.thegreenway.auth.FirebaseUserLiveData = authRepository.firebaseUserLiveData
    val isAuth: LiveData<Boolean> = authRepository.isAuth
    val message: LiveData<Event<String>> = authRepository.message

    init {
//        authRepository.exit.observeForever(
//            EventObserver {
//                _snackbarMessage.value = Event(
//                    getApplication<Application>()
//                        .applicationContext
//                        .getString(R.string.ui_exit_event)
//                )
//            })
    }
}

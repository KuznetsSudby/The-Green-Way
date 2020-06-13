package kusu.thegreenway.ui.main.account

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import kusu.thegreenway.R
import kusu.thegreenway.auth.AuthRepository
import kusu.thegreenway.auth.FirebaseUserLiveData
import kusu.thegreenway.utils.Event
import kusu.thegreenway.utils.EventObserver
import javax.inject.Inject

class AccountViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val user: FirebaseUserLiveData = authRepository.firebaseUserLiveData
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

package kusu.thegreenway.auth

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.FirebaseAuth
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kusu.thegreenway.common.Event

class FirebaseUserLiveData : LiveData<FirebaseUser?>() {

    private val firebaseAuth = FirebaseAuth.getInstance()

    private val _exit = MutableLiveData<Event<Unit>>()
    val exit: LiveData<Event<Unit>> = _exit

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        if (value != null && firebaseAuth.currentUser == null)
            _exit.value = Event(Unit)
        value = firebaseAuth.currentUser
    }

    override fun onActive() {
        firebaseAuth.addAuthStateListener(authStateListener)
    }

    override fun onInactive() {
        firebaseAuth.removeAuthStateListener(authStateListener)
    }
}
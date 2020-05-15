package kusu.thegreenway.utils

import android.text.Editable
import android.view.View
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

fun View.observeVisibility(lifecycleOwner: LifecycleOwner, isVisible: LiveData<Boolean>) {
    isVisible.observe(lifecycleOwner, Observer { visible ->
        visibility = if (visible) View.VISIBLE else View.GONE
    })
}

@ExperimentalCoroutinesApi
fun EditText.asFlow() = callbackFlow {
    val afterTextChanged: (Editable?) -> Unit = {
        offer(text.toString())
    }
    val textChangedListener = addTextChangedListener(afterTextChanged = afterTextChanged)

    awaitClose {
        removeTextChangedListener(textChangedListener)
    }
}
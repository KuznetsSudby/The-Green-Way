package kusu.thegreenway.common

import android.content.Context
import android.content.res.Resources
import android.text.Editable
import android.util.TypedValue
import android.view.View
import android.widget.EditText
import android.widget.Toast
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

fun String.toast(context: Context){
    toast(context, this)
}

fun toast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun Exception.messageOr(def: String): String = localizedMessage ?: message ?: def

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()

val Float.dp: Float
    get() = this * Resources.getSystem().displayMetrics.density

val Int.sp: Float
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    )


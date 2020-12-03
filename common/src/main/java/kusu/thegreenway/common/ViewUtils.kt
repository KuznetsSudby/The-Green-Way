package kusu.thegreenway.common

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.text.Editable
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
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

fun showKeyboard(activity: Context, view: EditText) {
    view.requestFocus();
    val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
}

fun hideKeyboardFrom(context: Context, view: View) {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun hideKeyboardFrom(context: Activity) {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(context.currentFocus?.windowToken, 0)
}

fun Fragment.getStatusBarHeight(): Int {
    var result = 0
    val resourceId = Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        result = Resources.getSystem().getDimensionPixelSize(resourceId)
    }
    return result
}


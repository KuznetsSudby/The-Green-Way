package kusu.thegreenway.preferences
import android.content.SharedPreferences

abstract class BasePreference<T>(val key: String,
                                 val store: SharedPreferences,
                                 val listener: ModifyListener<T>? = null,
                                 val isLog: Boolean = true,
                                 val isCommit: Boolean = false) {

    abstract fun getDefault(): T

    abstract fun getStored(): T?

    abstract fun setStored(value: T): SharedPreferences.Editor

    var value: T
        get() {
            (getStored() ?: getDefault()).apply {
                listener?.customGet(this)
                return this
            }
        }
        set(value) {
            if (isCommit) setStored(value).commit() else setStored(value).apply()
            listener?.customSet(value)
        }

    fun clear() {
        store.edit().remove(key).apply()
        listener?.customSet(getDefault())
    }

    fun exist(): Boolean {
        return store.contains(key)
    }
}

open class PreferenceString(key: String,
                            val def: String = "",
                            store: SharedPreferences,
                            listener: ModifyListener<String>? = null,
                            isLog: Boolean = true,
                            isCommit: Boolean = false)
    : BasePreference<String>(key, store, listener, isLog, isCommit) {

    override fun setStored(value: String): SharedPreferences.Editor = store.edit().putString(key, value)

    override fun getStored(): String? = store.getString(key, getDefault())

    override fun getDefault(): String = def
}

open class PreferenceStringSet(key: String,
                            val def: Set<String> = hashSetOf<String>(),
                            store: SharedPreferences,
                            listener: ModifyListener<Set<String>>? = null,
                            isLog: Boolean = true,
                            isCommit: Boolean = false)
    : BasePreference<Set<String>>(key, store, listener, isLog, isCommit) {

    override fun setStored(value: Set<String>): SharedPreferences.Editor = store.edit().putStringSet(key, value)

    override fun getStored(): Set<String>? = store.getStringSet(key, getDefault())

    override fun getDefault(): Set<String> = def
}

open class PreferenceInt(key: String,
                         val def: Int = 0,
                         store: SharedPreferences,
                         listener: ModifyListener<Int>? = null,
                         isLog: Boolean = true,
                         isCommit: Boolean = false)
    : BasePreference<Int>(key, store, listener, isLog, isCommit) {

    override fun setStored(value: Int): SharedPreferences.Editor = store.edit().putInt(key, value)

    override fun getStored(): Int? = store.getInt(key, getDefault())

    override fun getDefault(): Int = def
}

class PreferenceLong(key: String,
                     val def: Long = 0,
                     store: SharedPreferences,
                     listener: ModifyListener<Long>? = null,
                     isLog: Boolean = true,
                     isCommit: Boolean = false)
    : BasePreference<Long>(key, store, listener, isLog, isCommit) {

    override fun setStored(value: Long): SharedPreferences.Editor = store.edit().putLong(key, value)

    override fun getStored(): Long? = store.getLong(key, getDefault())

    override fun getDefault(): Long = def
}

open class PreferenceBoolean(key: String,
                             val def: Boolean = false,
                             store: SharedPreferences,
                             listener: ModifyListener<Boolean>? = null,
                             isLog: Boolean = true,
                             isCommit: Boolean = false)
    : BasePreference<Boolean>(key, store, listener, isLog, isCommit) {

    override fun setStored(value: Boolean): SharedPreferences.Editor = store.edit().putBoolean(key, value)

    override fun getStored(): Boolean? = store.getBoolean(key, getDefault())

    override fun getDefault(): Boolean = def

    fun toggle() {
        value = !value
    }
}

class PreferenceFloat(key: String,
                      val def: Float = 0f,
                      store: SharedPreferences,
                      listener: ModifyListener<Float>? = null,
                      isLog: Boolean = true,
                      isCommit: Boolean = false)
    : BasePreference<Float>(key, store, listener, isLog, isCommit) {

    override fun setStored(value: Float): SharedPreferences.Editor = store.edit().putFloat(key, value)

    override fun getStored(): Float? = store.getFloat(key, getDefault())

    override fun getDefault(): Float = def
}

class PreferenceComplexString(key: String,
                              val defPref: PreferenceString,
                              store: SharedPreferences,
                              listener: ModifyListener<String>? = null,
                              isLog: Boolean = true,
                              isCommit: Boolean = false)
    : PreferenceString(key, "", store, listener, isLog, isCommit) {

    override fun getDefault(): String = defPref.value
}

class PreferenceComplexBoolean(key: String,
                               val defPref: PreferenceBoolean,
                               store: SharedPreferences,
                               listener: ModifyListener<Boolean>? = null,
                               isLog: Boolean = true,
                               isCommit: Boolean = false)
    : PreferenceBoolean(key, false, store, listener, isLog, isCommit) {

    override fun getDefault(): Boolean = defPref.value

}

class PreferenceComplexStrBoolean(key: String,
                                  val defPref: PreferenceString,
                                  val expr: String,
                                  store: SharedPreferences,
                                  listener: ModifyListener<Boolean>? = null,
                                  isLog: Boolean = true,
                                  isCommit: Boolean = false)
    : PreferenceBoolean(key, false, store, listener, isLog, isCommit) {

    override fun getDefault(): Boolean = defPref.value == expr

}

class PreferenceComplexInt(key: String,
                           val defPref: PreferenceInt,
                           store: SharedPreferences,
                           listener: ModifyListener<Int>? = null,
                           isLog: Boolean = true,
                           isCommit: Boolean = false)
    : PreferenceInt(key, 0, store, listener, isLog, isCommit) {

    override fun getDefault(): Int = defPref.value
}

open class ModifyListener<T> {
    open fun customGet(value: T) {}

    open fun customSet(value: T) {}
}

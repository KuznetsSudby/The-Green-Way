package kusu.thegreenway.utils

sealed class Result<out R> {

    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
        }
    }

    inline fun proceedResult(success: (data: R) -> Unit, error: (exception: Exception) -> Unit) {
        if (this is Success) {
            success.invoke(data)
        } else {
            error.invoke((this as Error).exception)
        }
    }
}
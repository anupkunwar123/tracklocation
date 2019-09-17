package com.anupkunwar.locationtracker


sealed class Result<out R> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(
        val exception: Exception? = null,
        val errorCode: String? = null,
        val errorMessage: String? = null,
        val status: String? = null
    ) :
        Result<Nothing>()

    object Loading : Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
            Loading -> "Loading"
        }
    }

}

val Result<*>.succeeded
    get() = this is Result.Success && data != null
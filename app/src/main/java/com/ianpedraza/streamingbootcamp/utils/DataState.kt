package com.ianpedraza.streamingbootcamp.utils

/**
 * Reference: https://github.com/android/architecture-samples/blob/main/app/src/main/java/com/example/android/architecture/blueprints/todoapp/data/Result.kt
 */
sealed class DataState<out R> {

    data class Success<out T>(val data: T) : DataState<T>()

    data class Error(val exception: Exception) : DataState<Nothing>()

    object Loading : DataState<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success [data=$data]"
            is Error -> "Error [exception=$exception]"
            Loading -> "Loading"
        }
    }
}

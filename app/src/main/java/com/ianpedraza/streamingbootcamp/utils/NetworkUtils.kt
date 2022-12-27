package com.ianpedraza.streamingbootcamp.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class NetworkUtils {
    companion object {
        fun <T> makeNetworkCall(
            call: suspend () -> T
        ): Flow<DataState<T>> = flow {
            emit(DataState.Loading)
            try {
                val data = call.invoke()
                emit(DataState.Success(data))
            } catch (e: Exception) {
                emit(DataState.Error(e))
            }
        }
    }
}

package com.example.common.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

sealed class NetworkResult<T> {
    class Loading<T> : NetworkResult<T>()
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error<T>(val message: String, val code: Int? = null) : NetworkResult<T>()
    data class Exception<T>(val e: Throwable) : NetworkResult<T>()
}

fun <T> safeApiCall(apiCall: suspend () -> Response<T>): Flow<NetworkResult<T>> = flow {
    emit(NetworkResult.Loading())
    try {
        val response = apiCall()
        if (response.isSuccessful) {
            response.body()?.let {
                emit(NetworkResult.Success(it))
            } ?: emit(NetworkResult.Error("Empty response body", response.code()))
        } else {
            emit(NetworkResult.Error(response.message(), response.code()))
        }
    } catch (e: Exception) {
        emit(NetworkResult.Exception(e))
    }
}

// For APIs that don't return Response wrapper directly in interface
fun <T> safeApiCallDirect(apiCall: suspend () -> T): Flow<NetworkResult<T>> = flow {
    emit(NetworkResult.Loading())
    try {
        emit(NetworkResult.Success(apiCall()))
    } catch (e: Exception) {
        emit(NetworkResult.Exception(e))
    }
}

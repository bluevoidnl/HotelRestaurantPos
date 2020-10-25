package com.spuyt.assignment

sealed class Resource <out T>{
    data class Success<T>(val data: T):Resource<T>()
    data class Loading<T>(val intermediateResult: T?):Resource<T>()
    data class Error<T>(val throwable:Throwable):Resource<T>()
}
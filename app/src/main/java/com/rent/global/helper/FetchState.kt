package com.rent.global.helper

sealed class FetchState {

    object Fetching : FetchState()
    object Refreshing : FetchState()
    data class FetchDone(val isEmpty: Boolean) : FetchState()
    data class FetchError(val throwable: Throwable) : FetchState()

}
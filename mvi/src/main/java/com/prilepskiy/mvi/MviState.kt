package com.prilepskiy.mvi

interface MviState {
    val error: String?
    val isLoading: Boolean
}

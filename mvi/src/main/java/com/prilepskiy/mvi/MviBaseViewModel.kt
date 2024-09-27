package com.prilepskiy.mvi

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

abstract class MviBaseViewModel<S : MviState, A : MviAction, I : MviIntent>() : ViewModel() {

    var viewState by mutableStateOf(initState())
    abstract var reducer: Reducer<A, S>
    private val actions = MutableSharedFlow<A>()
    init {
        actions.onEach { action ->
            viewState = reducer.reduce(action, viewState)
        }.launchIn(viewModelScope)
    }
    abstract fun initState(): S
    abstract fun handleIntent(intent: I)
    fun onIntent(intent: I) {
        handleIntent(intent)
    }
    fun onAction(action: A) {
        viewModelScope.launch {
            actions.emit(action)
        }
    }
}

package com.prilepskiy.presentation.map

import com.prilepskiy.mvi.MviAction
import com.prilepskiy.mvi.MviIntent
import com.prilepskiy.mvi.MviState

sealed class MapIntent : MviIntent{
    data class OnError(val error:String):MapIntent()
    data class OnLoading(val isLoading:Boolean):MapIntent()
}

sealed class MapAction: MviAction {
    data class OnError(val error:String):MapAction()
    data class OnLoading(val isLoading:Boolean):MapAction()
}

data class MapState(
    override val error: String? = null,
    override val isLoading: Boolean = false,
):MviState
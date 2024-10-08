package com.prilepskiy.common

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ResourceProvider @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun getContext(): Context = context
}

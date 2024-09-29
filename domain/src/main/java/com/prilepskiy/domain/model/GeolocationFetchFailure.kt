package com.prilepskiy.domain.model

import com.prilepskiy.common.Failure

data class GeolocationFetchFailure(val throwable: Throwable) : Failure
package com.ligomezm.firebasecourse.network

import java.lang.Exception

interface RealTimeDataListener<T> {

    fun onDataChange(updateData: T)

    fun onError(exception: Exception)
}


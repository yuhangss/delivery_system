package com.example.delivery.common

/**
 * 回调接口
 */
interface HttpCallBack {
    fun onError(errorLog: String)
    fun onSuccess(message: String)
}
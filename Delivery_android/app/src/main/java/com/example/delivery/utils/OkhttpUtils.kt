package com.example.delivery.utils

import com.example.delivery.common.HttpCallBack
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * 封装http请求
 */

class OkhttpUtils {
    companion object {
        private var instance:OkhttpUtils? = null
        private var okhttp:OkHttpClient? =null

        @Synchronized
        fun getInstance() : OkhttpUtils {
            if(instance == null) {
                instance = OkhttpUtils()
            }
            okhttp = OkHttpClient().newBuilder()
                .retryOnConnectionFailure(true)
                .connectTimeout(10000, TimeUnit.SECONDS) //连接超时
                .readTimeout(10000, TimeUnit.SECONDS) //读取超时
                .writeTimeout(10000, TimeUnit.SECONDS) //写超时
                .build()
            return instance!!
        }
    }

    /**
     * Get请求
     * @url 请求地址
     * @map 请求参数,用于合成最终的url
     * form表单
     * @httpCallBack 回调
     */

    fun getRequest(url: String, map: HashMap<String, String>,
                   httpCallBack: HttpCallBack) {
        var urlRequest: String = ""
        var urlConcat: String = ""
        if(map.size > 0) { //有请求参数，对url进行拼接
            val list = ArrayList(map.keys)
            for(key in map.keys) {
                urlRequest += if(list[map.size - 1].equals(key)) {
                    key + "=" + map[key] //url末尾不拼接&符号
                }
                else {
                    key + "=" + map[key] + "&"
                }
            }
        }
        urlConcat = "$url?"
        urlConcat += urlRequest

        val request: Request = Request.Builder().url(urlConcat).build()
        val call: Call? = okhttp?.newCall(request)
        call?.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                httpCallBack.onError(e.printStackTrace().toString())
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let { httpCallBack.onSuccess(it.string()) }
                response.close()
            }
        })
    }



    /**
     * post请求,form表单
     * @url 请求地址
     * @map 请求参数
     * @httpCallBack 回调
     */

    fun postRequest(url: String, map: HashMap<String, String>,
                    httpCallBack: HttpCallBack) {
        val builder: FormBody.Builder = FormBody.Builder()
        if(map.size > 0) {
            for (key in map.keys) {
                key.let { builder.add(it, map[it].toString()) }
            }
        }

        val formBody:FormBody = builder.build()
        val request: Request = Request.Builder().url(url)
            .post(formBody)
            .build()
        val call: Call? = okhttp?.newCall(request)
        call?.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                httpCallBack.onError(e.printStackTrace().toString())
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let { httpCallBack.onSuccess(it.string()) }
            }
        })

    }

    /**
     * 以post的方式向后端发送机器人状态信息(报文)
     * RequestBody
     * @url 请求地址
     * @msg 报文
     * @httpCallBack 回调
     */

    fun postRequest(url: String, msg: String? = null, httpCallBack: HttpCallBack) {
        val requestBody = msg?.let {
            //创建requestBody,json形式
            val contentType: MediaType = "application/json;charset=utf-8".toMediaType()
            msg.toRequestBody(contentType)
        }?: run {
            FormBody.Builder().build()
        }

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        val call: Call? = okhttp?.newCall(request)
        call?.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                httpCallBack.onError(e.printStackTrace().toString())
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let { httpCallBack.onSuccess(it.string()) }
            }
        })

    }

    /**
     * post请求,form表单
     * @url 请求地址
     * @map 请求参数
     * @httpCallBack 回调
     */

    fun postRequest_Robot(url: String, map: HashMap<String, String>,
                    httpCallBack: HttpCallBack) {
        val builder: FormBody.Builder = FormBody.Builder()
        if(map.size > 0) {
            for (key in map.keys) {
                key.let { builder.add(it, map[it].toString()) }
            }
        }

        val formBody:FormBody = builder.build()
        val request: Request = Request.Builder().url(url)
            .post(formBody)
            .header("Authorization", "oUNEAoxbw2OOCA2jqEbM3Q==")
            .build()
        val call: Call? = okhttp?.newCall(request)
        call?.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                httpCallBack.onError(e.printStackTrace().toString())
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let { httpCallBack.onSuccess(it.string()) }
            }
        })

    }

    /**
     * Get请求
     * @url 请求地址
     * @map 请求参数,用于合成最终的url
     * form表单
     * @httpCallBack 回调
     */

    fun getRequest_Robot(url: String, httpCallBack: HttpCallBack) {

        val request: Request = Request.Builder().url(url).header("Authorization", "oUNEAoxbw2OOCA2jqEbM3Q==").build()
        val call: Call? = okhttp?.newCall(request)
        call?.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                httpCallBack.onError(e.printStackTrace().toString())
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let { httpCallBack.onSuccess(it.string()) }
                response.close()
            }
        })
    }

}


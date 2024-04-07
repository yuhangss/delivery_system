package com.example.delivery.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import com.alibaba.fastjson.JSON
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.delivery.common.Constants
import com.example.delivery.common.GlobalVariables
import com.example.delivery.common.HttpCallBack
import com.example.delivery.common.bean.UserDTO
import com.example.delivery.common.order.OrderState
import com.example.delivery.common.order.Receiver
import com.example.delivery.common.order.Robot
import com.example.delivery.common.order.Sender
import org.json.JSONObject
import java.io.BufferedInputStream

/**
 * 订单处理方法
 * @author syh
 */
object OrderUtils {
    /**
     * 从ResponseBody的字符串格式中获取到初始订单信息
     * 将获取到的初始信息暂存到Sender中，供显示订单信息时使用
     * @param msg ResponseBody.body.string
     */
    fun initOrder(msg: String): Int {
        val jsonObject = JSONObject(msg)
        val code = jsonObject.optInt("code")
        if(code != 200) {
            return code
        }

        Sender.sex = jsonObject.optString("msg")

        val dataJson = jsonObject.optJSONObject("data") ?: return code
        OrderState.id = dataJson.optInt("id")
        Sender.name = dataJson.optString("sender")
        Sender.id = dataJson.optString("senderid")
        Sender.url = dataJson.optString("senderurl")
        Sender.src = dataJson.optString("src")
        Robot.src_uuid = dataJson.optString("src_uuid")

        return code
    }

    /**
     * 将获取到收件人后的所有订单信息整合为json格式的字符串
     */
    fun toJSON(): String {
        return "{\n" +
                "\t\"id\": \"${OrderState.id}\",\n" +
                "\t\"sender\": \"${Sender.name}\",\n" +
                "\t\"senderid\": \"${Sender.id}\",\n" +
                "\t\"senderurl\": \"${Sender.url}\",\n" +
                "\t\"src\": \"${Sender.src}\",\n" +
                "\t\"receiver\": \"${Receiver.name}\",\n" +
                "\t\"receiverid\": \"${Receiver.id}\",\n" +
                "\t\"receiverurl\": \"${Receiver.url}\",\n" +
                "\t\"dest\": \"${Receiver.dest}\",\n" +
                "\t\"robot_name\": \"${GlobalVariables.name}\",\n" +
                "\t\"state\": \"${OrderState.state}\"\n" +
                "}"
    }

    /**
     * 处理查询用户的信息
     */
    fun queryResult(msg: String): Int {
        val json = JSONObject(msg)
        val code = json.optInt("code")
        if(code != 200) {
            return code
        }
        GlobalVariables.users.clear() //清空列表中原有的元素
        val dataJsons = json.getJSONArray("data")
        for (i in 0 until dataJsons.length()) {
            val dataJson = JSONObject(dataJsons.get(i).toString())
            GlobalVariables.users.add(UserDTO(
                dataJson.optString("username"),
                dataJson.optString("studentid"),
                dataJson.optString("location"),
                dataJson.optString("faceurl"),
                dataJson.optString("sex")
            )
            )
        }
        return code
    }

    /**
     * 处理更新订单的返回值
     */
    fun updateOrder(msg: String): Int {
        val jsonObject = JSONObject(msg)
        val code = jsonObject.optInt("code")
        if(code != 200) {
            return code
        }
        Robot.dest_uuid = jsonObject.optString("dest_uuid")
        return code
    }

    /**
     * 更新订单状态
     */
    fun updateState(state: String) {
        val hashMap = HashMap<String, String>()
        hashMap["state"] = state
        hashMap["id"] = OrderState.id.toString()
        OkhttpUtils.getInstance().postRequest(Constants.URL_UPDATE_STATE, hashMap, object : HttpCallBack {
            override fun onError(errorLog: String) {
                Log.i("DeliveryOrder", "更新状态连接失败 $state")
            }

            override fun onSuccess(message: String) {
                val jsonObject = JSONObject(message)
                val code = jsonObject.optInt("code")
                if(code == 200) {
                    Log.i("deliveryOrder", "订单状态更新成功 $state")
                }
                else
                    Log.i("deliveryOrder", "订单状态更新失败 $state")
            }
        })
    }

    /**
     * 处理机器人移动请求返回参数
     */
    fun isMoving(msg: String): Boolean {
        val jsonObject = JSONObject(msg)
        if(jsonObject.optString("error_msg") == "NONE" && jsonObject.optInt("error_code") == 0) {
            return true
        }
        return false
    }

    /**
     * 从获取的url中获取图片并保存到全局变量bitmap中
     */
    fun getBitmapFromURL(url: String, context: Context) {
        Glide.with(context)
            .asBitmap()
            .load(url)
            .into(object : CustomTarget<Bitmap>() {
                override fun onLoadCleared(placeholder: Drawable?) {
                    Log.i("getBitmap", "error")
                }

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    GlobalVariables.bitmap = resource
                    Log.i("getBitmap", "success")
                }
            })
    }
}
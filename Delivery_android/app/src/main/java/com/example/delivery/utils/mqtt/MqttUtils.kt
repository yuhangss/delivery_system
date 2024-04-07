package com.example.delivery.utils.mqtt

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.example.delivery.common.Constants
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*

object MqttUtils {
    private const val TAG = "MqttUtils"
    private const val host = Constants.HOST
    private const val username = ""
    private const val password =""
    private var client_id = "" //随机数+时间，动态生成


    @SuppressLint("StaticFieldLeak")
    private lateinit var mqttClient: MqttAndroidClient

    private lateinit var mMqttConnectOptions: MqttConnectOptions

//    //订阅主题的回调，通过参数传递，便于在activity中调用返回结果
//    private var mqttCallback = object : MqttCallback {
//        override fun connectionLost(cause: Throwable?) {
//            Log.i(TAG, "断开连接")
//        }
//
//        override fun deliveryComplete(token: IMqttDeliveryToken?) {
//
//        }
//
//        override fun messageArrived(topic: String?, message: MqttMessage?) {
//            Log.i(TAG, "收到消息: " + message?.payload.toString())
//        }
//    }

    //MQTT连接成功的监听
    private var iMqttActionListener = object : IMqttActionListener {
        override fun onSuccess(asyncActionToken: IMqttToken?) {
            Log.i(TAG, "连接成功")
            try {
                mqttClient.subscribe(Constants.SUB_TOPIC, 0) //订阅主题，参数服务质量qos为0
            }
            catch (e: MqttException) {
                e.printStackTrace()
            }
        }

        override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
            exception?.printStackTrace()
            Log.i(TAG, "连接失败")
//            doClientConnection(context) //重连
        }
    }

    /**
     * 初始化MQTT
     */
    fun init(context: Context, mqttCallback: MqttCallback) {
        client_id += System.currentTimeMillis()
        val serverURL = host //协议+地址+端口号
        mqttClient = MqttAndroidClient(context, serverURL, client_id)
        mqttClient.setCallback(mqttCallback) //设置鉴定订阅消息的回调
        mMqttConnectOptions = MqttConnectOptions()
        mMqttConnectOptions.isCleanSession = true //设置是否清楚缓存
        mMqttConnectOptions.connectionTimeout = 30 //设置超时时间 秒
        mMqttConnectOptions.keepAliveInterval = 60 //设置心跳包发送间隔 秒
        mMqttConnectOptions.userName = username //设置用户名
        mMqttConnectOptions.password = password.toCharArray() //设置密码

        doClientConnection(context)
    }

    /**
     * 连接MQTT服务器
     */
    private fun doClientConnection(context: Context) {
        if(!mqttClient.isConnected && isConnectIsNormal(context)) {
            try {
                mqttClient.connect(mMqttConnectOptions, null, iMqttActionListener)
            }
            catch (e: MqttException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 检查网络连接是否正常
     * 检测网络状态，监听网络变化
     * API > 23?
     */
    private fun isConnectIsNormal(context: Context): Boolean {
        //获得ConnectivityManager对象
        val connectivityManager: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        if(network != null) {
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
            if(networkCapabilities != null) {
                if(networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    //wifi
                    return true
                }
                else if(networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    //移动数据
                    return true
                }
            }
        }
        return false
    }

    /**
     * 断开连接
     */
    fun disconnect() {
        try {
            mqttClient.disconnect()
        }
        catch (e: MqttException) {
            e.printStackTrace()
        }
    }

}
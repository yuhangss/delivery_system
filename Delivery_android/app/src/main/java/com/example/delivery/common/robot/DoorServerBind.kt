package com.example.delivery.common.robot

import android.annotation.SuppressLint
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.robint.ihotellib.IHotelCallback
import com.robint.ihotellib.IHotelInterface

class DoorServerBind {
    /**
     * @property instance 单例
     */
    companion object {
        @SuppressLint("StaticFieldLeak")
        var instance: DoorServerBind? = null

        @JvmName("getInstance1")
        @Synchronized
        fun getInstance(): DoorServerBind {
            if(instance == null) {
                instance = DoorServerBind()
            }
            return instance!!
        }
    }

    private var context: Context? = null

    private val TAG: String = DoorServerBind::class.java.simpleName
    private var iHotelInterface: IHotelInterface? = null

    private val hotelCallBack = object : IHotelCallback.Stub() {
        override fun onDoorState(state: String?) {
//            Log.i(TAG, "door state: $state")
        }
//        fun open() {
//            iHotelInterface!!.controlCargoDoor(1)
//        }
//        fun close() {
//            iHotelInterface!!.controlCargoDoor(2)
//        }
//        fun stop() {
//            iHotelInterface!!.controlCargoDoor(3)
//        }
    }

    private val mTouchDeathRecipient = IBinder.DeathRecipient { Log.i(TAG, "binderDied") }

    private val doorConn: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.i(TAG, "onServiceConnected")
            iHotelInterface = IHotelInterface.Stub.asInterface(service)
            if(iHotelInterface == null) {
                Log.i(TAG, "bind fail")
            }
            else {
                try {
                    service?.linkToDeath(mTouchDeathRecipient, 0)
                    iHotelInterface!!.setCallback(hotelCallBack)
                }
                catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.i(TAG, "onServiceDisconnected")
            iHotelInterface = null
        }
    }

    /**
     * 获取context
     */
    fun init(context: Context) {
        this.context = context
    }

    fun bindServer() {
        val intent = Intent()
        intent.action = "com.robint.serialservice.HotelService"
        intent.setPackage("com.robint.serialservice")
        context!!.startService(intent)
        val b = context!!.bindService(intent, doorConn, Service.BIND_AUTO_CREATE)
        Log.i(TAG, "开始绑定机器人服务: $b")
    }

    fun getInterface(): IHotelInterface {
        return iHotelInterface!!
    }

    fun unbindServer() {
        context!!.unbindService(doorConn)
    }
}
package com.example.delivery.common.robot

import android.annotation.SuppressLint
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import com.example.delivery.utils.speech.FileLogger
import com.example.delivery.utils.speech.ShellUtils
import com.robint.robintspeech.IDMTaskCallback
import com.robint.robintspeech.ISpeechInterface
import com.robint.robintspeech.ISpeechListener
import com.robint.robintspeech.ITTSCallback

class SpeechServerBind {
    companion object {
        @SuppressLint("StaticFieldLeak")
        var instance: SpeechServerBind? = null

        @JvmName("getInstance1")
        @Synchronized
        fun getInstance(): SpeechServerBind {
            if (instance == null) {
                instance = SpeechServerBind()
            }
            return instance!!
        }
    }

    private var context: Context? = null

    private val TAG: String = SpeechServerBind::class.java.simpleName
    private var isAidlBindServer: Boolean = false
    private var speechServerBind: SpeechServerBind? = null
    //绑定stub
    private var mSpeechService: ISpeechInterface? = null

    private var isGateReturn: Boolean = false

    private lateinit var ittsCallback: ITTSCallback

    fun setIttsCallback(ittsCallback: ITTSCallback) {
        this.ittsCallback = ittsCallback
    }

    private lateinit var idmTaskCallback: IDMTaskCallback.Stub

    fun setIdmTaskCallback(idmTaskCallback: IDMTaskCallback.Stub) {
        this.idmTaskCallback = idmTaskCallback
    }

    private lateinit var iSpeechListener: ISpeechListener.Stub

    fun setISpeechListener(iSpeechListener: ISpeechListener.Stub) {
        this.iSpeechListener = iSpeechListener
    }

    private val mDeathRecipient: IBinder.DeathRecipient = IBinder.DeathRecipient {
        @Override
        fun binderDied() {
            FileLogger.e(TAG, "binderDied")

            //停止服务
            val commandResult: ShellUtils.CommandResult =ShellUtils.execCommand(
                "am force-stop com.robint.serialservice",
                true, true)

            if(commandResult.result == 0) {
                FileLogger.e(TAG, "force-stop success")
            }
            //重新绑定 间隔5秒
            startTimer.start()
        }
    }

    private val startTimer: CountDownTimer = object : CountDownTimer(5 * 1000, 1000) {
        override fun onTick(l: Long) {}
        override fun onFinish() {
            FileLogger.d(
                TAG, "onFinish and start bind service"
            )
            val intent = Intent()
            /**
             * 修改服务
             */
            intent.action = "com.robint.robintspeech.MainService"
            intent.setPackage("com.robint.robintspeech")
            context!!.startService(intent)
            val b = context!!.bindService(intent, mSpeechConn, Service.BIND_AUTO_CREATE)
            Log.e("SpeechServer", "开始绑定机器人语音服务：$b")
        }
    }

    private val mSpeechConn: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            Log.d(TAG, "onServiceConnected")
            mSpeechService = ISpeechInterface.Stub.asInterface(service)
            if (mSpeechService == null) {
                Log.d(TAG, "bind fail")
            } else {
                Log.d(
                    TAG, mSpeechService.toString() + ""
                )
                try {
//                    mSpeechService.setParamChange(ALIAS_KEY,"154878358469sefsadvbdefsda");
                    mSpeechService?.setListener(iSpeechListener)
                    mSpeechService?.setTTSListener(ittsCallback)
                    mSpeechService?.setDMTaskCallback(idmTaskCallback)
                    service.linkToDeath(mDeathRecipient, 0)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            mSpeechService = null
        }
    }

    fun init(context: Context) {
        this.context = context
    }

    fun getStub(): ISpeechInterface? {
        return mSpeechService
    }

    /**
     * 单例
     */
    fun getInstance(): SpeechServerBind? {
        if (speechServerBind == null) {
            synchronized(SpeechServerBind::class.java) {
                if (speechServerBind == null) {
                    speechServerBind =
                        SpeechServerBind()
                }
            }
        }
        return speechServerBind
    }

    /**
     * bind service
     */
    fun bindServer(
        ittsCallback: ITTSCallback.Stub?,
        idmTaskCallback: IDMTaskCallback.Stub?,
        speechListener: ISpeechListener.Stub?
    ) {
        this.ittsCallback = ittsCallback!!
        this.idmTaskCallback = idmTaskCallback!!
        iSpeechListener = speechListener!!
        if (!isAidlBindServer) {
            FileLogger.d(TAG, "bindServer")
            /**
             * 修改服务
             */
            val intent = Intent()
            intent.action = "com.robint.robintspeech.MainService"
            intent.setPackage("com.robint.robintspeech")
            context!!.startService(intent)
            val b = context!!.bindService(intent, mSpeechConn, Service.BIND_AUTO_CREATE)
            Log.e("SpeechServer", "开始绑定机器人语音服务：$b, 169")
            checkTimer.start()
        }
    }

    private val checkTimer: CountDownTimer = object : CountDownTimer(10 * 1000, 1000) {
        override fun onTick(l: Long) {}
        override fun onFinish() {
            if (mSpeechService == null) {
                FileLogger.d(
                    TAG, "stub is null  rebind service"
                )
                /**
                 * 修改服务
                 */
                val intent = Intent()
                intent.action = "com.robint.robintspeech.MainService"
                intent.setPackage("com.robint.robintspeech")
                context!!.startService(intent)
                val b = context!!.bindService(intent, mSpeechConn, Service.BIND_AUTO_CREATE)
                Log.e("SpeechServer", "开始绑定机器人语音服务：$b, 189")
                start()
            }
        }
    }

    /**
     * @param context
     * @describe：解绑服务
     */
    fun unbindServer(context: Context) {
        if (isBindServer()) {
            try {
                context.unbindService(mSpeechConn)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    fun isBindServer(): Boolean {
        return isAidlBindServer
    }

}
package com.example.delivery

import android.content.Context
import android.graphics.BitmapFactory
import android.os.*
import android.util.Log
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.delivery.common.Constants
import com.example.delivery.common.GlobalVariables
import com.example.delivery.common.HttpCallBack
import com.example.delivery.common.UIVariables
import com.example.delivery.common.facenet.FaceNet
import com.example.delivery.common.order.OrderState
import com.example.delivery.common.order.Receiver
import com.example.delivery.common.order.Robot
import com.example.delivery.common.order.Sender
import com.example.delivery.common.robot.DoorServerBind
import com.example.delivery.common.robot.SpeechServerBind
import com.example.delivery.navigation.Screen
import com.example.delivery.ui.screens.CountdownScreen
import com.example.delivery.ui.screens.MainFrame
import com.example.delivery.ui.screens.OrderScreen
import com.example.delivery.ui.screens.SpeechScreen
import com.example.delivery.ui.theme.DeliveryTheme
import com.example.delivery.utils.OkhttpUtils
import com.example.delivery.utils.OrderUtils
import com.example.delivery.utils.RobotUtils
import com.example.delivery.utils.SentenceUtils
import com.example.delivery.utils.speech.FileLogger
import com.robint.ihotellib.IHotelCallback
import com.robint.robintspeech.IDMTaskCallback
import com.robint.robintspeech.ISpeechListener
import com.robint.robintspeech.ITTSCallback
import okhttp3.internal.wait
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap

class MainActivity : ComponentActivity() {
    companion object {
        const val TAG: String = "MainActivity"
    }

    private var handler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //首先要初始化所有的状态变量
        OrderState.initOrderState()
        Receiver.initReceiver()
        Robot.initRobot()
        Sender.initSender()

        //加载facenet模型
        Constants.faceNet = FaceNet(this)

        //获取context
        SpeechServerBind.getInstance().init(this)
        initFileLogger()
        FileLogger.d(TAG, "bindService")

        //绑定语音服务
        SpeechServerBind.getInstance().bindServer(ittsCallback, idmTaskCallback, mSpeechListener)

        //获取context
        DoorServerBind.getInstance().init(this)
        //绑定舱门服务
        DoorServerBind.getInstance().bindServer()

        //让内容显示在了系统导航栏(手势小白条)和状态栏的下层，这样会遮盖掉部分内容
        WindowCompat.setDecorFitsSystemWindows(window, false)
        // 沉浸状态栏和导航栏
        val window = window
        val decorView = window.decorView
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController?.let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        //设置状态栏文字反色
        val controller = window.decorView.systemUiVisibility
        window.decorView.systemUiVisibility = controller or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        //初始化全局变量bitmap,避免空异常
        GlobalVariables.bitmap = BitmapFactory.decodeStream(this.assets.open("1_001.jpg"))

//        初始化机器人信息
        Thread {
            OkhttpUtils.getInstance().getRequest_Robot(Constants.URL_INFO, object : HttpCallBack {
                override fun onError(errorLog: String) {
                    Log.i("Robot_Init", "网络连接失败")
                }

                override fun onSuccess(message: String) {
                    val jsonObject = JSONObject(message)

                    val dataJson = jsonObject.optJSONObject("data")
                    GlobalVariables.name = dataJson!!.optString("name")
                    GlobalVariables.robot_uuid = dataJson.optString("robot_uuid")
                    GlobalVariables.group = dataJson.optString("group")
                    GlobalVariables.ip = dataJson.optString("ip")
                    GlobalVariables.mac = dataJson.optString("mac")
                    Log.i("Robot_Init", "获取机器人信息成功 $dataJson")
                }
            })
        }.start()

//        SpeechServerBind.getInstance().getStub()?.enableWakeup()
//        SpeechServerBind.getInstance().getStub()?.addQuickStartWord("da kai she xiang tou", "打开摄像头", null)

        setContent {

            DeliveryTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    //路由
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = Screen.MainFrame.route) {
                        composable(route = Screen.MainFrame.route) {
                            MainFrame(navController = navController)
                        }
                        composable(
                            route = Screen.CameraX.route,
                        ) {
                            CameraX(navController = navController)
                        }
                        composable(
                            route = Screen.OrderScreen.route,
                        ) {
                            OrderScreen(navController)
                        }
                        composable(
                            route = Screen.SpeechScreen.route
                        ) {
                            SpeechScreen(navController)
                        }
                        composable(
                            route = Screen.FinishScreen.route
                        ) {
                            CountdownScreen(navController = navController)
                        }
                    }

                    handler = Handler(Looper.getMainLooper(), object : Handler.Callback {
                        override fun handleMessage(p0: Message): Boolean {
                            if(p0.what == 0x123) {
//                                navController.navigate(Screen.CameraX.route)
                                GlobalVariables.OPEN_CAMERA_SUCCESS.value = true
                            }
                            if(p0.what == 0x124) {
                                //导航到订单界面
                                navController.navigate(Screen.OrderScreen.route)
                            }
                            if(p0.what == 0x125) {
                                UIVariables.showDialog = true
                                navController.navigate(Screen.SpeechScreen.route)
                            }
                            if(p0.what == 0x126) {
                                navController.navigate(Screen.CameraX.route)
                            }
                            if (p0.what == 0x127) {
                                navController.navigate(Screen.FinishScreen.route)
                            }
                            return true
                        }
                    })

                    navController.navigate(Screen.MainFrame.route)
//                    TimerPost()
                    OrderTimer(this, handler!!)
                }
            }
        }
    }


    fun TimerPost() {
        val msg = GlobalVariables.getRobotMsg()
        Timer().schedule(object : TimerTask() {
            override fun run() {
                //定时上传机器人状态信息
                OkhttpUtils.getInstance().postRequest(Constants.URL_MSG, msg, object :
                    HttpCallBack {
                    override fun onError(errorLog: String) {
                        Log.i("robot_msg", "failure")
                    }
                    override fun onSuccess(message: String) {
//                        Log.i("robot_msg", "success")
                    }
                })
            }
        }, 0, 1000)
    }


    fun OrderTimer(context: Context, handler: Handler) {
        Timer().schedule(object : TimerTask() {
            override fun run() {

                //定时根据当前订单状态更改机器人状态，向服务器请求或更改订单信息
                if(GlobalVariables.STATE_ORDER == Constants.STATE_FREE) {
                    val map = HashMap<String, String>()
                    map["name"] = GlobalVariables.name
                    OkhttpUtils.getInstance().postRequest(Constants.URL_GET_ORDER, map, object : HttpCallBack {
                        override fun onError(errorLog: String) {
                            Log.i("DeliveryOrder", "获取订单时发生连接错误")
                        }

                        override fun onSuccess(message: String) {
                            val res = OrderUtils.initOrder(message)
                            if(res == 200) {
                            OrderUtils.getBitmapFromURL(Sender.url, context)
                                GlobalVariables.STATE_ORDER = Constants.STATE_MOVE_TO_SRC // 成功获取订单，将状态转为移动到收获位置状态
                                handler.sendEmptyMessage(0x124)
                            }
                            Log.i("DeliveryOrder", "$res, state: free")
                        }
                    })
                }

                if(GlobalVariables.STATE_ORDER == Constants.STATE_MOVE_TO_SRC) {
                    Log.i("DeliveryOrder", Robot.state)
                    if(Robot.state == "RUNNING") { //机器人正在移动
                        //判断机器人是否移动到src
                        RobotUtils.navigationState()
                        GlobalVariables.isMoving = true
                        //移动到src，将状态修改为取货状态
                    }
                    else if(Robot.state == "STOP" && Robot.reached && GlobalVariables.isMoving) {
                        GlobalVariables.STATE_ORDER = Constants.STATE_PICK
                        Robot.state = "Ready"
                        Robot.reached = false
                        GlobalVariables.isMoving = false
                        //到达提示音
                        val sex = if (Sender.sex == "男") { "先生" } else { "女士" }
                        OrderState.state = "110"
                        OrderUtils.updateState(OrderState.state)
                        RobotUtils.voicePrompts("${Sender.name}$sex，小洛已经到达您的位置，快来放置您的货物吧")
                    }
                    //PAUSE
                    else if(Robot.state == "PAUSE") {
                        RobotUtils.voicePrompts("客官请让一让，小洛正在送物")
                        RobotUtils.moveResume()
                        RobotUtils.navigationState()
                    }
                    else if(Robot.state == "Ready") { //机器人未移动，此时应该发送移动请求
                        RobotUtils.navigationState()
                        RobotUtils.moveCancel()
                        Thread.sleep(1000)
                        RobotUtils.moveTo(Robot.src_uuid)
                    }
                    else {
                        RobotUtils.navigationState()
                    }
                }

                if(GlobalVariables.STATE_ORDER == Constants.STATE_PICK) {
                    handler.sendEmptyMessage(0x123)
                    synchronized(Constants.FACE_LOCK) {
                        Constants.FACE_LOCK.wait()
                    }
                    GlobalVariables.ENABLE_SPEECH.value = true
                    synchronized(Constants.QUERY_LOCK) {
                        Constants.QUERY_LOCK.wait()
                    }
                    OrderState.state = "120"
                    val json = OrderUtils.toJSON()

                    OkhttpUtils.getInstance().postRequest(Constants.URL_UPDATE_ORDER, json, object : HttpCallBack {
                        override fun onError(errorLog: String) {
                            Log.i("DeliveryOrder", "更新订单时发生连接错误")
                        }

                        override fun onSuccess(message: String) {
                            val res = OrderUtils.updateOrder(message)
                            if(res == 200) {
                                GlobalVariables.STATE_ORDER = Constants.STATE_MOVE_TO_DEST //更新订单成功，将机器人状态更新为移动到送货地址状态
                                GlobalVariables.OPEN_CAMERA_SUCCESS.value = false
                                GlobalVariables.ENABLE_SPEECH.value = false
                            }
                            Log.i("DeliveryOrder", "$res, state: picking up")
                        }
                    })
                    while (GlobalVariables.STATE_ORDER != Constants.STATE_MOVE_TO_DEST) {  }
                }

                if(GlobalVariables.STATE_ORDER == Constants.STATE_MOVE_TO_DEST) {
                    if(Robot.state == "RUNNING") { //同MOVE_TO_SRC
                        //修改订单
                        GlobalVariables.isMoving = true
                        RobotUtils.navigationState()
                    }
                    else if(Robot.state == "STOP" && Robot.reached && GlobalVariables.isMoving) {
                        GlobalVariables.STATE_ORDER = Constants.STATE_DELIVERY //置为送货状态
                        Robot.state = "Ready"
                        Robot.reached = false
                        GlobalVariables.isMoving = false
                        OrderState.state = "121"
                        OrderUtils.updateState(OrderState.state)
                        //到达提示音
                        val sex = if (Receiver.sex == "男") { "先生" } else { "女士" }
                        RobotUtils.voicePrompts("${Receiver.name}$sex，您有货物待接收，快来接收您的货物吧")
                    }
                    else if(Robot.state == "PAUSE") {
                        RobotUtils.voicePrompts("客官请让一让，小洛正在送物")
                        RobotUtils.moveResume()
                    }
                    else if(Robot.state == "Ready"){

                        val hashMap = HashMap<String, String>()
                        hashMap["loc_name"] = Receiver.dest

                        OkhttpUtils.getInstance().postRequest(Constants.URL_QUERY_LOCATION, hashMap, object : HttpCallBack {
                            override fun onError(errorLog: String) {
                                Log.i("DeliveryOrder", "查询地址时连接错误")
                            }

                            override fun onSuccess(message: String) {
                                val jsonObject = JSONObject(message)
                                val code = jsonObject.optInt("code")
                                if(code == 402) {
                                    Log.i("DeliveryOrder", "空数据")
                                }
                                else {
                                    Robot.dest_uuid = jsonObject.optString("data")
                                    Log.i("DeliveryOrder", "获取地址: ${Robot.dest_uuid}")
                                }
                            }
                        })
                        Log.i("Delivery", Robot.dest_uuid)
                        Thread.sleep(2000)
                        RobotUtils.moveTo(Robot.dest_uuid)
                        RobotUtils.navigationState()
                    }
                    else {
                        RobotUtils.navigationState()
                    }
                }

                if(GlobalVariables.STATE_ORDER == Constants.STATE_DELIVERY) {
                    //语音播报提示、人脸识别、开仓、关仓
                    handler.sendEmptyMessage(0x123)

                    synchronized(Constants.FACE_LOCK) {
                        Constants.FACE_LOCK.wait()
                    }
                    GlobalVariables.ENABLE_FINISH.value = true
                    val hashMap = HashMap<String, String>()
                    OrderState.state = "122"
                    hashMap["id"] = OrderState.id.toString()
                    hashMap["state"] = "122"
                    OkhttpUtils.getInstance().postRequest(Constants.URL_UPDATE_STATE, hashMap, object : HttpCallBack {
                        override fun onError(errorLog: String) {
                            Log.i("DeliveryOrder", "完成订单时发生连接错误")
                        }

                        override fun onSuccess(message: String) {
                            val jsonObject = JSONObject(message)
                            if(jsonObject.optInt("code") == 200) {
                                handler.sendEmptyMessage(0x127) //打开订单完成界面
                                synchronized(Constants.FINISH_LOCK) { //获取锁
                                    Constants.FINISH_LOCK.wait() //阻塞线程，等待完成界面释放线程
                                }
                                GlobalVariables.ENABLE_FINISH.value = false
                                RobotUtils.voicePrompts("舱门即将关闭，感谢您的使用")
                            }
                            Log.i("DeliveryOrder", "${jsonObject.optInt("code")}, state: delivery")
                        }
                    })
                    while (GlobalVariables.STATE_ORDER != Constants.STATE_FREE) {}
                }
            }
        }, 0, 5000)
    }

    //初始化FileLogger
    private fun initFileLogger() {
        val externalDirPath = getExternalFilesDir(null)!!.absolutePath
        FileLogger.init(externalDirPath)
        FileLogger.d(TAG, "init -----------------")
    }

    private val ittsCallback: ITTSCallback.Stub =object : ITTSCallback.Stub() {
        override fun beginning(p0: String?) {

        }

        override fun error(p0: String?) {

        }

        override fun received(p0: ByteArray?) {

        }

        override fun end(p0: String?, p1: Int) {

        }
    }

    private val idmTaskCallback = object : IDMTaskCallback.Stub() {
        override fun onDMTaskResult(p0: String?, p1: Int): String {
            return ""
        }
    }

    private val mSpeechListener = object : ISpeechListener.Stub() {
        override fun onChatCall(p0: String?, p1: String?) {
            Log.d(TAG, "cmd $p0, data $p1")
        }

        override fun onError(p0: Int, p1: String?) {
            Log.d(TAG, "code $p0, msg $p1")
        }

        override fun onLocalCall(p0: String?, p1: String?) {
            Log.d(TAG, "cmd $p0, data $p1")
        }

        override fun onMediaCall(p0: String?, p1: String?) {
            Log.d(TAG, "cmd $p0, data $p1")
        }

        override fun onMessage(msg: String?, data: String?) {
            Log.d(TAG, "msg $msg, data $data")
            val message: Message
            when (msg) {
                "local_wakeup.result" -> {}
                "sys.wakeup.doa" -> {
                    Log.d(TAG, "--------------唤醒角度----------------")
                    val doa: Int
                    try {
                        val jsonObject = com.alibaba.fastjson.JSONObject.parseObject(data)
                        doa = jsonObject.getInteger("doa")
                        Log.d(TAG, "------------------doa : $doa")
                        Thread {
                            RobotUtils.startRotate("$doa")
                        }.start()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                "sys.dialog.state" -> when (data) {
                    "avatar.silence" -> refreshTv("等待唤醒...")
                    "avatar.listening" -> refreshTv("监听中...")
                    "avatar.understanding" -> refreshTv("理解中...")
                    "avatar.speaking" -> refreshTv("播放语音中...")
                }
                "context.input.text" -> {
                    try {
                        val jo = JSONObject(data)
                        if (jo.has("var")) {
                            val `var` = jo.optString("var", "")
                            refreshText(`var`)
                        }
                        if (jo.has("text")) {
                            Log.i("WakeUp", SpeechServerBind.getInstance().getStub()?.isWakeup.toString())
                            val text1 = jo.optString("text", "")
                            val res = SentenceUtils.getKeyword(text1)
                            Thread {
                                RobotUtils.checkRoom(res, handler)
                            }.start()
                            val tag = SentenceUtils.checkText(text1, "打开摄像头")
                            if(tag) {
                                SpeechServerBind.getInstance().getStub()?.stopDialog()
                                handler!!.sendEmptyMessage(0x126)
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }

                else -> {}
            }
        }

        override fun onPhotoCall(p0: String?, p1: String?) {
            Log.d(TAG, "cmd $p0, data $p1")
        }

        override fun onRosCall(p0: String?, p1: String?) {
            Log.d(TAG, "cmd $p0, data $p1")
        }

        override fun onUpdate(p0: Int, p1: String?) {
            Log.d(TAG, "cmd $p0, data $p1")
        }

        override fun onNativeApiQuery(p0: String?, p1: String?) {
            Log.d(TAG, "nativeApi $p0, data $p1")
        }
    }

    private fun refreshTv(text: String) {
        runOnUiThread { UIVariables.avatarText.value = text }
    }

    private fun refreshText(text: String) {
        runOnUiThread {
            UIVariables.inputText.value = text
        }
    }
}
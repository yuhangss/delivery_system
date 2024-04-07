package com.example.delivery.utils

import android.media.AudioManager
import android.os.Handler
import android.os.RemoteException
import android.util.Log
import com.example.delivery.common.Constants
import com.example.delivery.common.GlobalVariables
import com.example.delivery.common.HttpCallBack
import com.example.delivery.common.order.Robot
import com.example.delivery.common.robot.SpeechServerBind
import org.json.JSONObject

/**
 * 机器人执行操作函数
 * @see checkRoom
 * @see voicePrompts
 * @see moveTo
 * @see movePause
 * @see moveCancel
 * @see moveResume
 * @see moveToCharge
 * @see navigationState
 * @see startRotate
 */

object RobotUtils {
    /**
     * 提示用户语音
     */
    fun voicePrompts(text: String) {
        try {
            SpeechServerBind.getInstance().getStub()?.speakByID(text, 3, null, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE)
        }
        catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    /**
     * 检查房间号
     */
    fun checkRoom(res: String, handler: Handler?) {
        if (res == "没听到房间号") {
            voicePrompts("抱歉，没听清，请重新告诉我房间号")
        }
        else {
            val hashMap = HashMap<String, String>()
            hashMap["receiver"] = ""
            hashMap["dest"] = res
            OkhttpUtils.getInstance().postRequest(Constants.URL_FUZZY_QUERY, hashMap, object : HttpCallBack {
                override fun onError(errorLog: String) {
                    Log.i("QueryUsers", "网络请求失败")
                }

                override fun onSuccess(message: String) {
                    val code = OrderUtils.queryResult(message)
                    if(code != 200) {
                        Log.i("QueryUsers", "未查询到相关用户")
                        voicePrompts("没有找到用户哦")
                    }
                    else {
                        Log.i("QueryUsers", "查询到以下用户 $message")
                        handler?.sendEmptyMessage(0x125)
                        voicePrompts("找到以下用户，请选择一个用户")
                    }
                }
            })
        }
    }

    /**
     * 机器人移动到指定位置
     */
    fun moveTo(location_uuid: String) {
        val hashMap: HashMap<String, String> = HashMap()
        hashMap["json"] = "{\"command_name\": \"navigation_start\", \"location_uuid\": \"$location_uuid\"}"
        OkhttpUtils.getInstance().postRequest_Robot(Constants.URL_TASK, hashMap, object :
            HttpCallBack {
            override fun onError(errorLog: String) {
                Log.i("DeliveryOrder", "机器人移动请求错误,${GlobalVariables.STATE_ORDER}")
            }

            override fun onSuccess(message: String) {
                if(OrderUtils.isMoving(message)) {
                    Log.i("DeliveryOrder", "机器人移动请求成功，正在向目标地址移动,${GlobalVariables.STATE_ORDER}")
                }
                else {
                    Log.i("DeliveryOrder", message + "机器人任务被占用")
                    Robot.state = "Ready"
                }
            }
        })
    }

    /**
     * 机器人暂停任务
     */
    fun movePause() {
        val hashMap: HashMap<String, String> = HashMap()
        hashMap["json"] = "{\"command_name\": \"navigation_pause\"}"
        OkhttpUtils.getInstance().postRequest_Robot(Constants.URL_TASK, hashMap, object : HttpCallBack {
            override fun onError(errorLog: String) {
                Log.i("DeliveryOrder", errorLog + "暂停失败")

            }

            override fun onSuccess(message: String) {
                Log.i("DeliveryOrder", message + "暂停成功")
            }
        })
    }

    /**
     * 机器人继续任务
     */
    fun moveResume() {
        val hashMap: HashMap<String, String> = HashMap()
        hashMap["json"] = "{\"command_name\": \"navigation_resume\"}"
        OkhttpUtils.getInstance().postRequest_Robot(Constants.URL_TASK, hashMap, object : HttpCallBack {
            override fun onError(errorLog: String) {
                Log.i("DeliveryOrder", errorLog + "开始失败")

            }

            override fun onSuccess(message: String) {
                Log.i("DeliveryOrder", message + "开始成功")

            }
        })
    }

    /**
     * 机器人取消任务
     */
    fun moveCancel() {
        val hashMap: HashMap<String, String> = HashMap()
        hashMap["json"] = "{\"command_name\": \"navigation_cancel\"}"
        OkhttpUtils.getInstance().postRequest_Robot(Constants.URL_TASK, hashMap, object : HttpCallBack {
            override fun onError(errorLog: String) {
                Log.i("DeliveryOrder", errorLog + "返回失败")
            }

            override fun onSuccess(message: String) {
                Log.i("DeliveryOrder", message + "取消成功")

            }
        })
    }


    /**
     * 机器人返回充电桩1
     */
    fun moveToCharge() {
        val hashMap: HashMap<String, String> = HashMap()
        hashMap["json"] = "{\"command_name\": \"navigation_cancel\"}"
        OkhttpUtils.getInstance().postRequest_Robot(Constants.URL_TASK, hashMap, object : HttpCallBack {
            override fun onError(errorLog: String) {
                Log.i("DeliveryOrder", errorLog + "返回失败")
            }

            override fun onSuccess(message: String) {
                Log.i("DeliveryOrder", message + "取消成功")
                val hashMap1: HashMap<String, String> = HashMap()
                hashMap1["json"] = "{\"command_name\": \"navigation_start\", \"location_uuid\": \"a2ae9b83b77f4f7b8ecf688793b4d5a6\"}" //默认充电桩1
                OkhttpUtils.getInstance().postRequest_Robot(Constants.URL_TASK, hashMap1, object : HttpCallBack {
                    override fun onError(errorLog: String) {
                        Log.i("DeliveryOrder", errorLog)
                    }

                    override fun onSuccess(message: String) {
                        Log.i("DeliveryOrder", message + "返回充电桩")
                    }
                })
            }
        })
    }

    /**
     * 机器人导航状态查询接口
     */
    fun navigationState() {
        OkhttpUtils.getInstance().getRequest_Robot(Constants.URL_NAVIGATION_STATE, object : HttpCallBack {
            override fun onError(errorLog: String) {
                Log.i("NavigationState", "网络连接失败")
            }

            override fun onSuccess(message: String) {
                val jsonObject = JSONObject(message)
                val dataJSON = jsonObject.optJSONObject("data")
                val state = dataJSON!!.optString("state")
                val reached = dataJSON.optBoolean("reached")
                val location = dataJSON.optString("location_uuid")
                Robot.state = state
                Robot.reached = reached
                Log.i("NavigationState", "state: $state, reached: $reached, location: $location")
            }
        })
    }

    /**
     * 机器人旋转
     */
    fun startRotate(theta: String) {
        val hashMap: HashMap<String, String> = HashMap()
        hashMap["json"] = "{\"command_name\": \"motion_start_rotate\", \"theta_degree\": \"$theta\"}" //默认充电桩1
        OkhttpUtils.getInstance().postRequest_Robot(Constants.URL_TASK, hashMap, object : HttpCallBack {
            override fun onError(errorLog: String) {
                Log.i("StartRotate", "旋转失败")
            }

            override fun onSuccess(message: String) {
                Log.i("StartRotate", "开始旋转，旋转角度：$theta")
//                voicePrompts("小洛即将旋转，请给我一点空间")
            }
        })
    }

    /**
     *
     */

}
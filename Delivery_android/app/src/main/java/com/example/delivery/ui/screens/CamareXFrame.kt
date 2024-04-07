package com.example.delivery

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.os.Looper
import android.os.Message
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.example.delivery.common.Constants
import com.example.delivery.common.GlobalVariables
import com.example.delivery.common.facenet.FaceNet
import com.example.delivery.common.robot.DoorServerBind
import com.example.delivery.navigation.Screen
import com.example.delivery.utils.BitmapUtils
import com.example.delivery.utils.RobotUtils
import okhttp3.internal.notifyAll
import java.util.logging.Handler

/**
 * 相机预览
 * 人脸识别
 */
@SuppressLint("UnsafeOptInUsageError")
@Composable
fun CameraX(navController: NavController) {
    RequestPermission()

    //生命周期所有者和Context
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    //provider
    val cameraProviderFuture = remember {
        ProcessCameraProvider.getInstance(context)
    }

    //构建previewView
    val previewView = remember {
        PreviewView(context).apply {
            id = R.id.preview_view
        }
    }
    val features = Constants.faceNet.recognizeImage(GlobalVariables.bitmap)

    val cameraHandler =
        android.os.Handler(Looper.getMainLooper(), object : android.os.Handler.Callback {
            override fun handleMessage(p0: Message): Boolean {
                if (p0.what == 0x300) {
                    Toast.makeText(context, "人脸识别成功", Toast.LENGTH_SHORT).show()
                    val text = "身份验证成功"
                    RobotUtils.voicePrompts(text)
                    Log.i("FaceNet", "人脸识别成功")
                    //关闭摄像头
                    cameraProviderFuture.get().unbindAll()
                    RobotUtils.startRotate("180")
                    DoorServerBind.getInstance().getInterface().controlCargoDoor(1) //开舱
                    navController.popBackStack()
                    synchronized(Constants.FACE_LOCK) {
                        Constants.FACE_LOCK.notifyAll()
                    }
                    navController.navigate(Screen.SpeechScreen.route)
                }
                return true
            }
        })


    Box(modifier = Modifier.fillMaxSize()) {
        //将相机预览的对象添加到Compose中
        AndroidView(factory = {previewView}, modifier = Modifier.fillMaxSize()) {
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = androidx.camera.core.Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                //默认镜头
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                //获取预览数据
                val imageAnalysis = ImageAnalysis.Builder()
                    .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                    .setTargetRotation(android.view.Surface.ROTATION_0) //旋转180
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(context)) {
                    Thread {
                        //处理预览数据
                        val bitmap_camera = BitmapUtils.getBitmap(it)
                        val features_camera = bitmap_camera?.let { it1 -> Constants.faceNet.recognizeImage(it1) }
                        val dist = features_camera?.let { it1 -> features.compare(it1) }
                        if (dist != null) {
                            Log.i("FaceNet", dist.toString())
                            //欧式距离小于1，视为识别成功
                            if(dist < 1) {
                                cameraHandler.sendEmptyMessage(0x300)
                            }
                        }
                        it.close()
                    }.start()
                }

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalysis
                    )
                } catch (e: Exception) {
                    Log.e("CameraXException", e.toString())
                }
            }, ContextCompat.getMainExecutor(context))
        }
    }
}

@Composable
private fun RequestPermission() {
    // 基于 LocalComposition 获取 Context
    val context = LocalContext.current

    // 基于 LocalLifecycleOwner 获取 Lifecycle
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    // 定义需要动态获取的 Permission 类型
    val permission = Manifest.permission.CAMERA

    // Result API 调用时的 launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            //判断权限申请结果，并根据结果显示不同画面，由于 onResult 不是一个 @Composable lambda，所以不能直接显示 Composalbe 需要通过修改 state 等方式间接显示 Composable
            Log.i("Permission", "Granted: $isGranted")
        }
    )

    // 在 Activity onStart 时，发起权限事情，如果权限已经获得则跳过
    val lifecycleObserver = remember {
        LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                if (!permission.isGrantedPermission(context)) {
                    launcher.launch(permission)
                }
            }
        }
    }

    // 当 Lifecycle 或者 LifecycleObserver 变化时注册回调，注意 onDispose 中的注销处理避免泄露
    DisposableEffect(lifecycle, lifecycleObserver) {
        lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycle.removeObserver(lifecycleObserver)
        }
    }
}

private fun String.isGrantedPermission(context: Context): Boolean {
    // 判断是否已经后的状态
    return context.checkSelfPermission(this) == PackageManager.PERMISSION_GRANTED
}
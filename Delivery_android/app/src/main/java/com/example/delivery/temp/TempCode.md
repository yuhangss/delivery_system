# 临时代码保存

## FaceNet模型验证代码
```kotlin
        val faceNet = FaceNet(this)

        //图片
        val bitmap_1 = BitmapFactory.decodeStream(assets.open("1_001.jpg"))
        val bitmap_1_1 = BitmapFactory.decodeStream(assets.open("1_002.jpg"))
        val bitmap_2 = BitmapFactory.decodeStream(assets.open("2_001.jpg"))

        val features_1 = faceNet.recognizeImage(bitmap_1)
        val features_1_1 = faceNet.recognizeImage(bitmap_1_1)
        val features_2 = faceNet.recognizeImage(bitmap_2)

        val dist_1 = features_1.compare(features_1_1)
        val dist_2 = features_1.compare(features_2)

        println("相同人脸欧式距离:$dist_1")
        println("不同人脸欧式距离:$dist_2")
```

## 创建文件并写入代码
```kotlin
val tmp = assets.open("facenet.pt")
val out = FileOutputStream(this.externalCacheDir?.absolutePath +
        File.separator + "facenet.pt")
var read: Int = -1
try {
    tmp.use { input ->
        out.use {
            while ({read = input.read(); read}() != -1) {
                it.write(read)
            }
        }
    }
} catch (e: Exception) {
    e.printStackTrace()
}
```

## 系统导航栏设置
- 设置状态栏和导航栏，11开始已弃用
```kotlin
        //设置状态栏透明
        window.statusBarColor = Color.Transparent.value.toInt()
        //撑满屏幕(剩余?)处理导航栏遮盖问题
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
```

- 设置系统导航栏padding
```kotlin
modifier = Modifier.navigationBarsPadding()
```

- 获取状态栏高度
```kotlin
        //获取状态栏高度
        var statusBarHeight = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if(resourceId > 0) {
            statusBarHeight = resources.getDimensionPixelSize(resourceId)
        }
```

## CameraX拍照 ImageCapture
```kotlin
    //图像捕获用例
    val imageCapture = remember {
        ImageCapture.Builder().build()
    }

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {
                fileUtils.createDirectoryNotExits(context)
                val file = fileUtils.createFile(context)
                //用于存储新捕获图像的选项
                val outputOption = ImageCapture.OutputFileOptions.Builder(file).build()
                //调用线程执行器、调用回调
                imageCapture.takePicture(outputOption, ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageSavedCallback{
                        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                            //没有错误，保存uri
                            val saveUri = Uri.fromFile(file)
                            Toast.makeText(context, saveUri.path, Toast.LENGTH_SHORT).show()
                        }

                        override fun onError(exception: ImageCaptureException) {
                            Log.e("----图片保存失败----", exception.toString())
                        }
                    })
            }) {
                Icon(imageVector = Icons.Filled.Face, contentDescription = null)
            }
        }
```

## 手动处理json数据
```kotlin
val message = msg.replace("\"", "").replace("{", "").replace("}", "")
        val data = message.split(",")

        if(data[0].replace("code:", "").toInt() != 200) {
            return data[1].replace("msg:", "")
        }

        OrderState.is_accepted = "1" //取货状态标志为真
        OrderState.id = data[2].replace("data:id:", "").toInt()
        Sender.name = data[3].replace("sender:", "")
        Sender.id = data[4].replace("senderid:", "")
        Sender.url = data[5].replace("senderurl:", "")
        Sender.src = data[7].replace("src:", "")                                                                                                                
        GlobalVariables.STATE_ORDER = Constants.STATE_PICK // 成功获取订单，将状态转为取货状态
        return "获取订单成功"


val message = msg.replace("\"", "").replace("{", "").replace("}", "")
val data = message.split(",")
var temp = data[0].split(":")
if(temp[1].toInt() != 200) {
    temp = data[1].split(":")
    return temp[1]
}
GlobalVariables.STATE_ORDER = Constants.STATE_DELIVERY //更新订单成功，将机器人状态更新为送货状态
return "更新订单成功"

val message = msg.replace("\"", "").replace("{", "").replace("}", "")
val data = message.split(",")
var temp = data[0].split(":")
if(temp[1].toInt() != 200) {
    temp = data[1].split(":")
    return temp[1]
}

Sender.initSender()
Receiver.initReceiver()
OrderState.initOrderState()
GlobalVariables.STATE_ORDER = Constants.STATE_FREE //订单完成，将机器人重新置为空闲状态
return "订单完成"
```

```ts
<template>
    <div>
        <div id="main" style="width: 500px; height: 400px;">

        </div>
    </div>
</template>

<script>
import * as echarts from 'echarts'

export default {
    name: "Home",
    setup() {
        
    },

    mounted() { //页面元素渲染之后触发
        var chartDom = document.getElementById('main');
        var myChart = echarts.init(chartDom);
        var option;

        option = {
            xAxis: {
                type: 'category',
                data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
            },
            yAxis: {
                type: 'value'
            },
            series: [
                {
                data: [150, 230, 224, 218, 135, 147, 260],
                type: 'line'
                }
            ]
        };
        myChart.setOption(option)
    }
}
</script>

<style scoped>

</style>
```

```java
    /**
     * 小程序向后端请求机器人是否到达收件位置
     * @param delivery 必须包含订单号，便于查询订单
     * @return Result
     */
    @PostMapping("/user/arrived_src")
    public Result UArriveSrc(@RequestBody Delivery delivery) {
        QueryWrapper<Delivery> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", delivery.getId());
        Delivery order = deliveryMapper.selectOne(queryWrapper);
        if(order == null) {
            return Result.error(Constants.CODE_400, "订单号错误");
        }
        if(order.getIs_arrived_src().equals("0")) {
            return Result.error(Constants.CODE_600, "机器人未到达src");
        }
        else if(order.getIs_arrived_src().equals("1")) {
            return Result.success("机器人到达src");
        }
        else {
            return Result.error(Constants.CODE_500, "系统错误，订单有误，请联系管理员");
        }
    }


    /**
     * 小程序向后端请求订单是否被接收
     * @param delivery 未更新订单
     * @return 查询更新订单的结果
     */
    @PostMapping("/user/accept")
    public Result queryOrder(@RequestBody Delivery delivery) {
        if(CodeUtils.checkSender(delivery)) {
            return Result.error(Constants.CODE_400, "参数错误");
        }

        QueryWrapper<Delivery> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", delivery.getId());
        queryWrapper.eq("is_accepted", "1");
        Delivery order = deliveryMapper.selectOne(queryWrapper);
        if(order == null) {
            return Result.error(Constants.CODE_402, "当前无此订单");
        }
        return Result.success(order.getIs_accepted());
    }

    /**
     * 小程序向后端请求机器人是否到达送件位置
     * @param delivery 必须包含订单号，便于查询订单
     * @return Result
     */
    @PostMapping("/user/arrived_dest")
    public Result UArriveDest(@RequestBody Delivery delivery) {
        QueryWrapper<Delivery> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", delivery.getId());
        Delivery order = deliveryMapper.selectOne(queryWrapper);
        if(order == null) {
            return Result.error(Constants.CODE_400, "订单号错误");
        }
        if(order.getIs_arrived_dest().equals("0")) {
            return Result.error(Constants.CODE_600, "机器人未到达dest");
        }
        else if(order.getIs_arrived_dest().equals("1")) {
            return Result.success("机器人到达dest");
        }
        else {
            return Result.error(Constants.CODE_500, "系统错误，订单有误，请联系管理员");
        }
    }


    /**
     * 订单完成接口,当小程序端查询到订单后请求该接口
     * @param delivery 订单信息
     * @return 未被接收(避免该情况)、未完成、完成 3种情况
     */
    @PostMapping("/user/finish")
    public Result finishOrder(@RequestBody Delivery delivery) {
        QueryWrapper<Delivery> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", delivery.getId());
        queryWrapper.eq("is_accepted", "1");
        queryWrapper.eq("is_finished", "1");
        Delivery order = deliveryMapper.selectOne(queryWrapper);
        if(order == null) {
            queryWrapper.clear();
            queryWrapper.eq("id", delivery.getId());
            queryWrapper.eq("is_accepted", "1");
            queryWrapper.eq("is_finished", "0");
            order = deliveryMapper.selectOne(queryWrapper);
            if(order == null) {
                return Result.error(Constants.CODE_402, "当前无此订单，可能未被机器人接收");
            }
            return Result.error(Constants.CODE_402, "当前订单未完成");
        }
        return Result.success(order.getIs_finished());
    }
```

```java
    /**
     * 结束送货任务后，更新订单信息
     * @param delivery 订单
     * @return 操作结果
     */
    @PostMapping("/robot/finish")
    public Result finishOrderR(@RequestBody Delivery delivery) {
        if(CodeUtils.checkSender(delivery) || CodeUtils.checkReceiver(delivery)) {
            return Result.error(Constants.CODE_400, "参数错误");
        }

        QueryWrapper<Delivery> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", delivery.getId());
        queryWrapper.eq("is_accepted", "1");
        queryWrapper.eq("is_finished", "0");
        Delivery one = deliveryMapper.selectOne(queryWrapper);
        if(one == null) {
            return Result.error(Constants.CODE_402, "当前没有该待处理订单");
        }
        return Result.success(deliveryMapper.update(delivery, queryWrapper));
    }
```
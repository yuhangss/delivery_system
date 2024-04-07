使用说明

## 1.使用说明

### (1). 配置文件

​    将ispeechservice-debug.aar 放在工程的libs目录下
​    修改文件
​    file: app/build.gradle

    repositories {
        flatDir {
            dirs 'libs'//this way we can find the .aar file in libs folder
        }
    }
    
    dependencies {
        **
        ..
        **
    
        implementation(name: 'ispeechservice-debug', ext: 'aar')
    }

### (2).启动时绑定服务

​    /**
​     * 绑定服务
​     * @param view
​     */

```
/**
 * bind service
 **/
public void bindServer(ITTSCallback.Stub ittsCallback, IDMTaskCallback.Stub idmTaskCallback, ISpeechListener.Stub speechListener) {
    this.ittsCallback = ittsCallback;
    this.idmTaskCallback = idmTaskCallback;
    this.iSpeechListener = speechListener;
    if(!isAidlBindServer) {
        FileLogger.d(TAG, "bindServer");

        Intent intent = new Intent();
        intent.setAction("com.robint.robintspeech.MainService");
        intent.setPackage("com.robint.robintspeech");
        APP.getInstance().startService(intent);
        APP.getInstance().bindService(intent, mSpeechConn , Service.BIND_AUTO_CREATE);
    }
}
    private ServiceConnection mSpeechConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected");
            mSpeechService = ISpeechInterface.Stub.asInterface(service);
            if(mSpeechService == null){
                Log.d(TAG, "bind fail");
            }else {
                Log.d(TAG, mSpeechService + "");
                try {
//                    mSpeechService.setParamChange(ALIAS_KEY,"154878358469sefsadvbdefsda");
                    mSpeechService.setListener(iSpeechListener);
                    mSpeechService.setTTSListener(ittsCallback);
                    mSpeechService.setDMTaskCallback(idmTaskCallback);
                    service.linkToDeath(mDeathRecipient, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mSpeechService = null;
        }
    };
```

## 2，语音接口说明文档 ISpeechInterface接口

### 1,注册快捷唤醒词

```java
	// 应以注册本地命令唤醒词
    void registerCharCommand(in String[] cmds);
    void registerLocalCommand(in String[] cmds);
    void registerMediaCommand(in String[] cmds);
    void registerPhotoCommand(in String[] cmds);
    void registerRosCommand(in String[] cmds);
	// 应以注销本地命令唤醒词
    void unregisterCharCommand();
    void unregisterLocalCommand();
    void unregisterMediaCommand();
    void unregisterPhotoCommand();
    void unregisterRosCommand();
```

### 2,设置对调监听

```java
   void setListener(ISpeechListener listener);
```

### 3.开关唤醒

默认SDK加载后不会启动语音唤醒，您可以通过以下接口对需对语音唤醒进行控制。

```java
	// 使能语音唤醒
    void enableWakeup();
    // 关闭语音唤醒
    void disableWakeup();
    // 获取唤醒词
    String[] getWakeupWords();
```

### 4.开启/终止对话

```java
	// 开启对话。如果当前已经在对话中，则重新开启新一轮对话。
    void startDialog();
    // 关闭对话
    void stopDialog();
    // 点击唤醒/停止识别/打断播报 操作接口
    void avatarClick();
```

### 5.查询当前是否使能唤醒

```java
    // 查询当前是否已经在可唤醒状态
    boolean isWakeup();
```

### 6.语音播报

参数说明：
1）**text** 播报文本，支持[SSML](https://www.duiopen.com/docs/ct_ssml)。
2）**priority** 优先级

提供4个优先级播报。
①优先级0：保留，与DDS语音交互同级，仅限内部使用；
②优先级1：正常，默认选项，同级按序播放；
③优先级2：重要，可以插话<优先级1>，同级按序播放，播报完毕后继续播报刚才被插话的<优先级1>；
④优先级3：紧急，可以打断当前正在播放的<优先级1|优先级2>，同级按序播放，播报完毕后不再继续播报刚才被打断的<优先级1｜优先级2>。
3）**ttsId** 用于追踪该次播报的id，建议使用UUID。
4）**audioFocus** 该次播报的音频焦点，默认值:
①优先级0：android.media.AudioManager#AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE
②优先级非0：android.media.AudioManager#AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK

```java
    // 请求语音播报
    void speak(String text, int level, String id);
```

停止播报接口如下:
1）ttsId与speak接口的ttsId一致，则停止或者移除该播报；
2）ttsId为空， 停止所有播报；
3）ttsId为"0"，停止当前播报。

```java
  // 停止播报
    void shutUp(String arg);
```

### 7.TTS相关设置

SDK定制不同的合成音类型，可以在需要的时候调用下面的接口。

```java
    /**
     * 设置TTS播报语速的接口
     * 调用此接口则云端配置的合成音语速失效，此后的合成音语速都将由此接口来托管
     *
     * @param speed 语速，取值0.5-2.0，0.5语速最快，2.0语速最慢，对应控制台上合成音倍率值
     */
    void setSpeed(float speed);
    /**
     * 设置TTS播报音量的接口
     * 调用此接口则云端配置的合成音音量失效，此后的合成音音量都将由此接口来托管
     *
     * @param volume 音量大小，取值1-100
     */
    void setVolume(int v);
    /**
     * 设置TTS引擎为本地或者云端
     *
     * @param mode 取值 TTSEngine.LOCAL(本地合成)，TTSEngine.CLOUD(云端合成)
     */
    void setMode(int mode);
    /**
     * 设置TTS播报的通道
     * @param streamType AudioManager.STREAM_*的取值
     * @throws DDSNotInitCompleteException
     */
    void setStreamType(int streamType);

    /**
     * 设备抢焦点,默认为抢焦点
     * @param enable true/false  true: 抢焦点  false: 不抢焦点
     */
    void enableFocus(boolean enable);
    /**
     * 设置TTS播报的通道(安卓7.0版本以上支持,并且需要配置K_AUDIO_USAGE和K_CONTENT_TYPE)
     * @param usage 取值: AudioAttributes.USAGE_*
     * @param contentType 取值: AudioAttributes.CONTENT_TYPE_*
     * @throws DDSNotInitCompleteException
     */
    void setUsage(int usage, int contentType);
    /**
     * 获取TTS播报人
     */
    String getSpeaker();
    /**
     * 设置TTS播报类型的接口
     * 调用此接口则云端配置的合成音类型失效，此后的合成音类型都将由此接口来托管
     * @param speaker 取值如：zhilingf, gdgm等
     */
    void setSpeaker(String speaker);
    /**
     * 设置TTS播报类型的接口
     * 调用此接口则云端配置的合成音类型失效，此后的合成音类型都将由此接口来托管
     * @param speaker 取值如：zhilingf, gdgm等
     * @param path 取值如："sdcard/tts/zhilingf.bin"，自定义合成间类型的全路径
     */
    void setSpeakerLocal(String speaker, String path);
    /**
     * 获取tts音量
     */
    int getVolume();
    /**
     * 获取tts的速度
     */
    float getTTSSpeed();
	/**
	 * 更新当前设备所在的地区 ；由于没有gps，用以查询天气等
	 * @param address 市区的名字
	 * example "苏州市" or "北京市"
     */
	void setDDSLocation(String address)
```

注：发音人列表：

|          | 发音人名称             | voiceid            | 分类 | 性别 |
| -------- | ---------------------- | ------------------ | ---- | ---- |
| **精品** |                        |                    |      |      |
|          | **精品男声秋木**       | **qiumum_0gushi**  | 精品 | 男声 |
|          | **精品电台男声考拉**   | kaolam_diantai     | 精品 | 男声 |
|          | **精品四川话女声文卿** | wqingf_csn         | 精品 | 女声 |
|          | **精品甜美女声小静**   | xjingfp            | 精品 | 女声 |
|          | **精品温柔女声小兰**   | gqlanfp            | 精品 | 女声 |
|          | **精品甜美女声小咪**   | xmamif             | 精品 | 女声 |
|          | **精品可爱男童连连**   | lzliafp            | 精品 | 男童 |
|          | **精品娱播女声麻豆**   | madoufp_yubo       | 精品 | 女声 |
|          | **精品甜美女声婷婷**   | xmguof             | 精品 | 女声 |
|          | **精品客服女声芳芳**   | gdfanfp            | 精品 | 女声 |
|          | **精品温柔女声麻豆**   | madoufp_wenrou     | 精品 | 女声 |
|          | **精品女声蓝雨**       | lanyuf             | 精品 | 女声 |
|          | **精品女声小浩**       | lucyfa             | 精品 | 女声 |
|          | **精品粤语女声何春**   | hchunf_ctn         | 精品 | 女声 |
|          | **精品男声小军**       | xijunma            | 精品 | 男声 |
|          | **精品甜美女神小玲**   | zhilingfp          | 精品 | 女声 |
|          | **精品女声安宁**       | aningfp            | 精品 | 女声 |
|          | **精品女学生初阳**     | cyangfp            | 精品 | 女声 |
|          | **精品客服女声小美**   | juan1f             | 精品 | 女声 |
|          | **精品粤语女声晓健**   | lunaif_ctn         | 精品 | 女声 |
|          | **精品欢快女神小玲**   | zhilingfp_huankuai | 精品 | 女声 |
|          | **精品知性女声晶晶**   | jjingfp            | 精品 | 女声 |
|          | **精品山东话女声大瑶** | dayaof_csd         | 精品 | 女声 |
|          | **精品娱报女声璃璃**   | lili1f_yubo        | 精品 | 女声 |
|          | **精品四川话女声胖胖** | ppangf_csn         | 精品 | 女声 |
|          | **精品女童贝壳**       | xbekef             | 精品 | 女童 |
|          | **精品上海话女声叶子** | yezi1f_csh         | 精品 | 女声 |
| **传统** |                        |                    |      |      |
|          | **传统可爱男童连连**   | lzliaf             | 传统 | 男童 |
|          | **传统甜美女神小玲**   | zhilingf           | 传统 | 女声 |
|          | **传统风趣幽默星哥**   | zxcm               | 传统 | 男声 |
|          | **传统邻家女声小妮**   | hyanif             | 传统 | 女声 |
|          | **传统平和女声小佚**   | anonyf             | 传统 | 女声 |
|          | **传统沉稳幽默纲叔**   | gdgm               | 传统 | 男声 |
|          | **传统男声小军**       | xijunm             | 传统 | 男声 |
|          | **传统可爱女童然然**   | qianranf           | 传统 | 女童 |
|          | **传统女童佚佚**       | anonyg             | 传统 | 女童 |
|          | **传统淡定风趣葛爷**   | geyou              | 传统 | 男声 |
| **标准** |                        |                    |      |      |
|          | **标准甜美女神小玲**   | zhilingfa          | 标准 | 女声 |
|          | **标准电台女声璃璃**   | lili1f_diantai     | 标准 | 女声 |
|          | **标准商务女声璃璃**   | lili1f_shangwu     | 标准 | 女声 |
|          | **标准女老师小妖**     | xiyaof_laoshi      | 标准 | 女声 |
|          | **标准女声安宁**       | aningf             | 标准 | 女声 |
|          | **标准女声杨阿姨**     | yaayif             | 标准 | 女声 |
|          | **标准高冷女声零八**   | linbaf_gaoleng     | 标准 | 女声 |
|          | **标准女老师行者**     | xizhef             | 标准 | 女声 |
|          | **标准男声考拉**       | kaolam             | 标准 | 男声 |
|          | **标准故事女声小静**   | xjingf_gushi       | 标准 | 女声 |
|          | **标准清新女声零八**   | linbaf_qingxin     | 标准 | 女声 |
|          | **标准男童堂堂**       | boy                | 标准 | 男童 |
|          | **标准女老师风吟**     | feyinf             | 标准 | 女声 |
|          | **标准女声小妖**       | xiyaof             | 标准 | 女声 |
|          | **标准可爱男童连连**   | lzliafa            | 标准 | 男童 |
|          | **标准女声瑶瑶**       | luyaof             | 标准 | 女声 |
|          | **标准鬼故事绝音**     | juyinf_guigushi    | 标准 | 女声 |
|          | **标准女学生初阳**     | cyangf             | 标准 | 女声 |
|          | **标准可爱女童然然**   | qianranfa          | 标准 | 女童 |
|          | **标准男声秋木**       | qiumum             | 标准 | 男声 |
|          | **标准男声小睿**       | tzruim             | 标准 | 男声 |
|          | **标准男声小江**       | wjianm             | 标准 | 男声 |
|          | **标准知性女声晶晶**   | jjingf             | 标准 | 女声 |
|          | **标准清新女声小妖**   | xiyaof_qingxin     | 标准 | 女声 |
|          | **标准磁性男声俞老师** | yukaim_all         | 标准 | 男声 |
|          | **标准女声朱株儿**     | zzherf             | 标准 | 女声 |
|          | **标准故事女声砖砖**   | zzhuaf             | 标准 | 女声 |
|          | **标准温柔女声小兰**   | gqlanf             | 标准 | 女声 |
|          | **标准男声季老师**     | jlshim             | 标准 | 男声 |
|          | **标准清亮女声小洁**   | smjief             | 标准 | 女声 |
|          | **标准邻家女声小妮**   | hyanifa            | 标准 | 女声 |
|          | **标准男童方方**       | gdfanf_boy         | 标准 | 男童 |
|          | **标准清纯女声考拉**   | kaolaf             | 标准 | 女声 |
|          | **标准飘逸女声小静**   | xjingf             | 标准 | 女声 |

### 8.触发指定意图

如果您期望主动触发某个意图的对话，可以在需要的时候调用下面的接口。


参数说明：

1）**skill** 技能名称，必填。
2）**task** 任务名称，必填。
3）**intent** 意图名称，必填。
4）**slots** 语义槽的key-value Json，可选。

```java
	/**
	参数说明：
    1）skill 技能名称，必填。
    2）task 任务名称，必填。
    3）intent 意图名称，必填。
    4）slots 语义槽的key-value Json，可选。
    */
    //跳过识别和语义，直接进入指定的意图对话。即：DDS主动向用户发起一轮对话。
    void triggerIntent(String skill, String task, String intent, String jsonObject);
```

### 9.场景模式

SDK提供了一些场景（如TTS播报/拾音距离）的切换，您可以在需要的时候调用下面的接口。

```java
    //TTS场景切换
    void setDDSMode(int mode);
```

### 10.输入文本

如果您期望跳过识别直接输入文本进行对话，可以在需要的时候调用下面的接口。
若当前没有在对话中，则以文本作为首轮说法，新发起一轮对话请求。
若当前正在对话中，则跳过识别，直接发送文本。

**参数说明**
**text** 输入的文本内容

```java
    void sendText(String text);
```

### 11.获取授权认证后的唯一ID

```java
    String getDeviceName();
```

### 12.获取是否初始化完成

```java
 /**
  * 获取sdk是否初始化完成
  * 完成返回true
  */
 boolean isInitComplete();
```

### 13.TTS事件回调

如果您想在TTS播报的相关时间节点收到通知，如：开始播报，播报结束。可以使用如下的方式：

```java
void setTTSListener(ITTSCallback callback);
```

### 14.更新用户词库

如果您期望更新用户的词库，使其能在技能中使用，可以在需要的时候调用下面的接口。上传结果可以通过sys.upload.result消息来获取。

注意：更新的词库仅在该设备下有效，不会影响其他设备。

```java
    /**
     * 向指定词库中添加词条
     */
	void insertVocab(String vocabName, in List<String> list);
 	/**
     * 清空之前上传到该词库的所有词条，然后添加词条
     */
    void clearInsertVocab(String vocabName, in List<String> vocabList);
	/**
     * 删除该词库的词条
     */
    void removeVocab(String vocabName, in List<String> list);
	/**
     * 清空之前上传到该词库的所有词条
     */
    void clearVocab(String vocabName);
 	/**
     * 删除该词库的词条
     */
    void deleteVocab(String vocabName, in List<String> list);
```

### 15.唤醒词相关

#### 15.1更新打断唤醒词

如果您期望在唤醒的时候能同时打断语音播报，可以在需要的时候调用下面的接口。打断唤醒词暂不支持在DUI控制台设置。（说明：打断需要设备开启回声消除）
参数说明：
1）**words** 打断唤醒词，为string数组，不为null。
2）**pinyins** 打断唤醒词的拼音，形如：ni hao xiao chi，为string数组，不为null。
3）**thresholds** 打断唤醒词的阈值，形如：0.120（取值范围：0-1）为string数组，不为null。

```java
    /**
     * 实时更新打断唤醒词-更新一条打断唤醒词
     * @param pinYin
     * @param word
     * @param threshold
     */
	void updateShortcutWakeupWord(String pinYin, String word, String threshold);
	/**
     * 实时移除打断唤醒词-移除多条打断唤醒词
     * @param words
     */
    void removeShortcutWakeupWords(in List<String> words);
	/**
     * 实时添加打断唤醒词-添加一条打断唤醒词
     * @param pinYin
     * @param word
     * @param threshold
     */
    void addShortcutWakeupWord(String pinYin, String word, String threshold);
```

#### 15.2添加/删除指定的快速唤醒词

如果您想通过代码来控制快速唤醒，可以调用下面的接口。

QuickStart词为类似“导航去”、“我想听”等，此类唤醒词只在oneshot模式下生效，作用为在未唤醒状态下语音输入“导航去天安门”，可直接进入对话流程。

*quickStart*

```java
    /**
     * 添加一条QuickStart词
     * @param pinYin
     * @param word
     * @param threshold
     */
	void addQuickStartWord(String pinYin, String word, String threshold);
    /**
     * 实时移除多条QuickStart词
     * @param words
     */
	void removeQuickStartWords(in List<String> words);
    /**
     * 更新一条QuickStart词的接口，覆盖原有的QuickStart词
     * @param pinYin
     * @param word
     * @param threshold
     */
	void updateQuickStartWord(String pinYin, String word, String threshold);
```

#### 15.3更新副唤醒词

如果您期望为设备增加一个副唤醒词，可以在需要的时候调用下面的接口。

参数说明：

1）**word** 副唤醒词，若设置null，则清空当前的副唤醒词。
2）**pinyin** 副唤醒词的拼音，形如：ni hao xiao chi。
3）**threshold** 副唤醒词的阈值，形如：0.120（取值范围：0-1）。若设置null，则自动估算。
4）**greetings** 副唤醒词的欢迎语，若设置null，则与主唤醒词保持一致。

```java
    /**
     * 更新副唤醒词
     * @param pinyin
     * @param word
     * @param threshold
     * @param greeting
     */
   void updateMinorWakeupWord(String pinyin, String word, String threshold, String greeting);
   /**
     * 获取当前副唤醒词
     * @return
     */
   String getMinorWakeupWord();
```

#### 15.4更新命令唤醒词（快捷唤醒词）

如果您期望在唤醒的时候执行一条指令，可以在需要的时候调用下面的接口。命令唤醒词和DUI控制台所设置的快捷唤醒词是相同的功能，且互不影响，可同时存在，根据需求来选择设置方式。
参数说明：
1）**actions** 命令唤醒词指令，为string数组，不为null。
2）**words** 命令唤醒词，为string数组，不为null。
3）**pinyins** 命令唤醒词的拼音，形如：ni hao xiao chi，为string数组, 不为null。
4）**thresholds** 命令唤醒词的阈值，形如：0.120（取值范围：0-1），为string数组，不为null。
5）**greetings** 命令唤醒词的欢迎语，为string二维数组，不为null，每维对应一个唤醒词的欢迎语。

```java
	/**
     * 实时更新命令唤醒词-更新一条命令唤醒词
     *
     * @param pinyin
     * @param word
     * @param threshold
     * @param greeting
     * @param action
     */
    void updateCommandWakeupWord(String pinyin, String word, String threshold, String greeting, String action);
    /**
     * 实时添加命令唤醒词-更新一条命令唤醒词
     *
     * @param pinyin
     * @param word
     * @param threshold
     * @param greeting
     * @param action
     */
    void addCommandWakeupWord(String pinyin, String word, String threshold, String greeting, String action);
    /**
     * 实时移除命令唤醒词-移除一条命令唤醒词
     * @param word
     */
    void removeCommandWakeupWord(String word);
    /**
     * 清空当前设置的命令唤醒词
     */
    void clearCommandWakeupWord();
```

#### 15.5添加/删除指定的主唤醒词

如果您想通过代码来控制主唤醒词，可以调用下面的接口。
注意：通过此接口添加的主唤醒词会屏蔽控制台添加的主唤醒词

```java

    /**
     * 实时移除主唤醒词-移除多条主唤醒词
     * @param words
     */
	void removeMainWakeupWords(in List<String> words);
    /**
     * 更新主唤醒词
     * @param pinyin
     * @param word
     * @param threshold
     * @param greeting
     */
    void updateMainWakeupWords(String pinyin, String word, String threshold, String greeting);
    /**
     * 添加指定主唤醒词
     * @param word
     * @param pinyin
     * @param greeting
     * @param threshold
     */
    void addMainWakeupWord(String pinyin, String word, String threshold, String greeting);
```

### 16.获取tts内核版本

```java
    String getWakeupVersion();
```

### 17.对话结果支持内容修改

```java
    void setDMTaskCallback(IDMTaskCallback callback);
```

## TTS回调接口ITTSCallback

如果您想在TTS播报的相关时间节点收到通知，如：开始播报，播报结束。可以使用如下的方式：

```java

    void beginning(String s);
    void received(inout byte[] bytes);
    void end(String ttsId, int status);
    void error(String s);
```

example

```java
	/**
     * 开始合成时的回调
     * @param ttsId 当前TTS的id， 对话过程中的播报ttsid默认为0，通过speak接口调用的播报，ttsid由speak接口指定。
     */
    @Override
    public void beginning(String ttsId) {
        Log.d(TAG, "TTS开始播报");
    }
    /**
     * 合成的音频数据的回调，可能会返回多次，data长度为0表示音频结束
     * @param data 音频数据属性：单声道 16bit, pcm
     */
    @Override
    public void received(byte[] data) {
        Log.d(TAG, "收到音频，此方法会回调多次，直至data为0，音频结束");
    }
    /**
     * TTS播报完成的回调
     * @param status 播报结束的状态。
     *               正常播报结束为0
     *               播报中途被打断结束为1
     */
    @Override
    public void end(String ttsId, int status) {
        Log.d(TAG, "TTS播报结束");
    }
    /**
     * 合成过程中出现错误的回调
     * @param error 错误信息
     */
    @Override
    public void error(String error) {
        Log.d(TAG, "出现错误，"+error);
    }

```

## 语音拦截接口IDMTaskCallback

此接口支持修改对话中的语音播报,修改文本展示等功能

```java
String onDMTaskResult(String dmTaskResult, int type);
```

example

```java
@Override
    public JSONObject onDMTaskResult(JSONObject dmTaskResult, Type type) {
        if (type == DMTaskCallback.Type.DM_OUTPUT) {// 处理dm的消息
            // 显示
            String display = dmTaskResult.optString("display");
            // 播报
            String nlg = dmTaskResult.optString("nlg");
            Log.d(TAG, "display = " + display);
            Log.d(TAG, "nlg = " + nlg);
            try {
                dmTaskResult.put("display", "修改之后的显示");
                dmTaskResult.put("nlg", "修改之后的播报");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (type == DMTaskCallback.Type.CDM_ERROR) {// // 处理对话异常的消息
            // 播报
            String nlg = dmTaskResult.optString("nlg");
            Log.d(TAG, "nlg = " + nlg);
            try {
                dmTaskResult.put("nlg", "修改之后的播报");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return dmTaskResult;
    }
```

## ISpeechListener注册监听回调

```java
/**
 * 注册快捷唤醒测回调
 */
void onChatCall(String cmd, String data);
void onLocalCall(String cmd, String data);
void onMediaCall(String cmd, String data);
void onPhotoCall(String cmd, String data);
void onRosCall(String cmd, String data);

/**
 * 更新进度
 */
void onUpdate(int type, String result);
/**
 * 消息推送
 */
void onMessage(String msg, String data);
/**
 * onQuery方法执行时，需要调用feedbackNativeApiResult来向DUI平台返回执行结果，表示一个native api执行结束。
 * native api的执行超时时间为10s
 */
void onNativeApiQuery(String nativeApi, String data);
/**
 * 错误信息回调
 */
void onError(int code, String error_msg);
```

### 响应内置消息

内置消息列表：

| message              | data                                                         | 说明                                                         |
| -------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| sys.kernel.ready     | 无                                                           | SDK加载完成，所有功能就绪                                    |
| sys.dialog.start     | { reason:"wakeup.major",//唤醒词触发对话 }                   | 对话开始，及开始原因： 1.wakeup.major，唤醒词唤醒 2.wakeup.command，快捷命令唤醒 3.api.startDialog，调用startDialog接口 4.api.sendText，调用sendText接口(对话Idle时) 5.api.triggerIntent，调用triggerIntent接口(对话Idle时) 6.api.avatarClick，调用avatarClick接口(对话Idle时) 7.api.avatarPress，调用avatarPress接口(对话Idle时) |
| sys.dialog.error     | { errId:071304, errMsg:"asr null" recordId:"70dcef50b8b31a6c02dbac1acee31de7" } | 对话中发生的错误                                             |
| sys.dialog.end       | { reason:"normal",//对话正常结束 skillId:"100001246" } { reason:"error",//对话发生错误； errId:071304, errMsg:"asr null", skillId:"100001246" } { reason:"interrupt",//对话被打断。强制结束对话时出现。 skillId:"100001246" } | 对话结束，及结束原因： 1.normal，对话正常结束 2.error，对话发生错误退出 3.interrupt，对话被打断退出 |
| sys.dialog.continue  | 无                                                           | 对话恢复                                                     |
| sys.resource.updated | 无                                                           | 收到线上资源变更的推送                                       |
| sys.wakeup.result    | { "type": "major", "word": "你好小驰", "greeting":"主人你好" } | 语音唤醒                                                     |
| sys.vad.begin        | 无                                                           | VAD触发                                                      |
| sys.vad.end          | 无                                                           | VAD结束                                                      |
| sys.upload.result    | { "result":false, "reqId":"xxxx", "errId":71801, "errMsg":"Network Invalid" } | 上传词库、设备信息的结果                                     |

example

```java
	@Override
    public void onMessage(final String message, final String data) {
        if (message.equals("sys.dialog.start")){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                     Toast.makeText(MainActivity.this, "对话开始了", Toast.LENGTH_LONG).show();
                }
            });
        }else if(message.equals("context.input.text")){
            final JSONObject dataJson = new JSONObject(data);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                     Toast.makeText(MainActivity.this, dataJson.optString("text"), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
```


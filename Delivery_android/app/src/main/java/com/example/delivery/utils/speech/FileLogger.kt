package com.example.delivery.utils.speech

import android.os.Process
import android.util.Log
import java.io.File
import java.io.FileFilter
import java.security.InvalidParameterException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors


object FileLogger {
    private val TAG = FileLogger::class.java.simpleName
    private val LOG_DATE_TIME_FORMAT = SimpleDateFormat("MM-dd HH:mm:ss.SSS")
    private val sLogExecutor = Executors.newSingleThreadExecutor()
    var isLogEnable = false
        private set
    private var sLogLevel: LogLevel = com.example.delivery.utils.speech.FileLogger.LogLevel.VERBOSE
    private var sLogFileManager: LogFileManager? = null
    private val limitLogMap: MutableMap<String, Int> = HashMap()

    /**
     * 设置Log开关
     *
     * @param enable 开关项(默认为开).
     */
    fun setEnable(enable: Boolean) {
        isLogEnable = enable
    }

    fun setLogLevel(level: LogLevel) {
        sLogLevel = level
    }

    /**
     * 设置写入log的文件夹
     *
     * @param dirPath 文件夹地址
     */
    fun init(dirPath: String) {
        isLogEnable = true
        val file = File(dirPath)
        Log.d("FileLogger", "dir$dirPath")
        if (!file.exists() || !file.isDirectory) {
            throw InvalidParameterException()
        }
        sLogFileManager = LogFileManager("$dirPath/filelogs")
    }

    /**
     * @param id the id for this log. Must be unique
     * @param cntTimesAfterLogOnce example: 1000 log once, then after 1000 call of this will log again
     */
    fun limitLog(id: String, tag: String, message: String?, cntTimesAfterLogOnce: Int) {
        if (!limitLogMap.containsKey(id)) {
            limitLogMap[id] = 0
        } else {
            val currentCnt = limitLogMap[id]
            if (currentCnt!! < cntTimesAfterLogOnce) {
                limitLogMap[id] = currentCnt + 1
                return
            } else {
                limitLogMap[id] = 0
            }
        }
        d(tag, message)
    }

    /**
     * log for debug
     *
     * @param message log message
     * @param tag     tag
     * @see Log.d
     */
    fun d(tag: String, message: String?) {
        if (isLogEnable) {
            Log.d(tag, message!!)
            writeToFileIfNeeded(
                tag,
                message,
                com.example.delivery.utils.speech.FileLogger.LogLevel.DEBUG
            )
        }
    }

    /**
     * log for debug
     *
     * @param message   log message
     * @param throwable throwable
     * @param tag       tag
     * @see Log.d
     */
    fun d(tag: String, message: String, throwable: Throwable?) {
        if (isLogEnable) {
            Log.d(tag, message, throwable)
            writeToFileIfNeeded(
                tag,
                """
                    $message
                    ${Log.getStackTraceString(throwable)}
                    """.trimIndent(), com.example.delivery.utils.speech.FileLogger.LogLevel.DEBUG
            )
        }
    }

    /**
     * log for debug
     *
     * @param tag    tag
     * @param format message format, such as "%d ..."
     * @param params message content params
     * @see Log.d
     */
    fun d(tag: String, format: String?, vararg params: Any?) {
        if (isLogEnable) {
            val msg = String.format(format!!, *params)
            Log.d(tag, msg)
            writeToFileIfNeeded(tag, msg, com.example.delivery.utils.speech.FileLogger.LogLevel.DEBUG)
        }
    }

    /**
     * log for warning
     *
     * @param message log message
     * @param tag     tag
     * @see Log.w
     */
    fun w(tag: String, message: String) {
        if (isLogEnable) {
            Log.w(tag, message)
            writeToFileIfNeeded(
                tag,
                message,
                com.example.delivery.utils.speech.FileLogger.LogLevel.WARN
            )
        }
    }

    /**
     * log for warning
     *
     * @param tag       tag
     * @param throwable throwable
     * @see Log.w
     */
    fun w(tag: String, throwable: Throwable?) {
        if (isLogEnable) {
            Log.w(tag, throwable)
            writeToFileIfNeeded(
                tag,
                Log.getStackTraceString(throwable),
                com.example.delivery.utils.speech.FileLogger.LogLevel.WARN
            )
        }
    }

    /**
     * log for warning
     *
     * @param message   log message
     * @param throwable throwable
     * @param tag       tag
     * @see Log.w
     */
    fun w(tag: String, message: String, throwable: Throwable?) {
        if (isLogEnable) {
            Log.w(tag, message, throwable)
            writeToFileIfNeeded(
                tag,
                """
                    $message
                    ${Log.getStackTraceString(throwable)}
                    """.trimIndent(), com.example.delivery.utils.speech.FileLogger.LogLevel.WARN
            )
        }
    }

    /**
     * log for warning
     *
     * @param tag    tag
     * @param format message format, such as "%d ..."
     * @param params message content params
     * @see Log.w
     */
    fun w(tag: String, format: String?, vararg params: Any?) {
        if (isLogEnable) {
            val msg = String.format(format!!, *params)
            Log.w(tag, msg)
            writeToFileIfNeeded(tag, msg, com.example.delivery.utils.speech.FileLogger.LogLevel.WARN)
        }
    }

    /**
     * log for error
     *
     * @param message message
     * @param tag     tag
     * @see Log.i
     */
    fun e(tag: String, message: String) {
        if (isLogEnable) {
            Log.e(tag, message)
            writeToFileIfNeeded(
                tag,
                message,
                com.example.delivery.utils.speech.FileLogger.LogLevel.ERROR
            )
        }
    }

    /**
     * log for error
     *
     * @param message   log message
     * @param throwable throwable
     * @param tag       tag
     * @see Log.i
     */
    fun e(tag: String, message: String, throwable: Throwable?) {
        if (isLogEnable) {
            Log.e(tag, message, throwable)
            writeToFileIfNeeded(
                tag,
                """
                    $message
                    ${Log.getStackTraceString(throwable)}
                    """.trimIndent(), com.example.delivery.utils.speech.FileLogger.LogLevel.ERROR
            )
        }
    }

    /**
     * log for error
     *
     * @param tag    tag
     * @param format message format, such as "%d ..."
     * @param params message content params
     * @see Log.e
     */
    fun e(tag: String, format: String?, vararg params: Any?) {
        if (isLogEnable) {
            val msg = String.format(format!!, *params)
            Log.e(tag, msg)
            writeToFileIfNeeded(tag, msg, com.example.delivery.utils.speech.FileLogger.LogLevel.ERROR)
        }
    }

    /**
     * log for information
     *
     * @param message message
     * @param tag     tag
     * @see Log.i
     */
    fun i(tag: String, message: String) {
        if (isLogEnable) {
            Log.i(tag, message)
            writeToFileIfNeeded(
                tag,
                message,
                com.example.delivery.utils.speech.FileLogger.LogLevel.INFO
            )
        }
    }

    /**
     * log for information
     *
     * @param message   log message
     * @param throwable throwable
     * @param tag       tag
     * @see Log.i
     */
    fun i(tag: String, message: String, throwable: Throwable?) {
        if (isLogEnable) {
            Log.i(tag, message, throwable)
            writeToFileIfNeeded(
                tag,
                """
                    $message
                    ${Log.getStackTraceString(throwable)}
                    """.trimIndent(), com.example.delivery.utils.speech.FileLogger.LogLevel.INFO
            )
        }
    }

    /**
     * log for information
     *
     * @param tag    tag
     * @param format message format, such as "%d ..."
     * @param params message content params
     * @see Log.i
     */
    fun i(tag: String, format: String?, vararg params: Any?) {
        if (isLogEnable) {
            val msg = String.format(format!!, *params)
            Log.i(tag, msg)
            writeToFileIfNeeded(tag, msg, com.example.delivery.utils.speech.FileLogger.LogLevel.INFO)
        }
    }

    /**
     * log for verbos
     *
     * @param message log message
     * @param tag     tag
     * @see Log.v
     */
    fun v(tag: String, message: String) {
        if (isLogEnable) {
            Log.v(tag, message)
            writeToFileIfNeeded(
                tag,
                message,
                com.example.delivery.utils.speech.FileLogger.LogLevel.VERBOSE
            )
        }
    }

    /**
     * log for verbose
     *
     * @param message   log message
     * @param throwable throwable
     * @param tag       tag
     * @see Log.v
     */
    fun v(tag: String, message: String, throwable: Throwable?) {
        if (isLogEnable) {
            Log.v(tag, message, throwable)
            writeToFileIfNeeded(
                tag,
                """
                    $message
                    ${Log.getStackTraceString(throwable)}
                    """.trimIndent(), com.example.delivery.utils.speech.FileLogger.LogLevel.VERBOSE
            )
        }
    }

    /**
     * log for verbose
     *
     * @param tag    tag
     * @param format message format, such as "%d ..."
     * @param params message content params
     * @see Log.v
     */
    fun v(tag: String, format: String?, vararg params: Any?) {
        if (isLogEnable) {
            val msg = String.format(format!!, *params)
            Log.v(tag, msg)
            writeToFileIfNeeded(tag, msg, com.example.delivery.utils.speech.FileLogger.LogLevel.VERBOSE)
        }
    }

    /**
     * 格式化log信息
     * @param tag
     * @param msg
     * @param logLevel
     */
    private fun writeToFileIfNeeded(tag: String, msg: String?, logLevel: LogLevel) {
        val strBuilder = StringBuilder()
        val stackTrace = Throwable().stackTrace
        val methodStackCnt = 2
        strBuilder
            .append(" ")
            .append(" tid=").append(Thread.currentThread().id)
            .append(" ")
            .append(stackTrace[methodStackCnt].fileName)
            .append("[").append(stackTrace[methodStackCnt].lineNumber)
            .append("] ").append("; ")
            .append(stackTrace[methodStackCnt].methodName)
            .append(": ")
        if (logLevel.value < sLogLevel.value || sLogFileManager == null) {
            return
        }
        sLogExecutor.execute { appendLog(strBuilder.toString() + tag, msg) }
    }

    private fun appendLog(tag: String, msg: String?) {
        val logMsg = formatLog(tag, msg)
        flushLogToFile(logMsg)
    }

    private fun flushLogToFile(logMsg: String) {
        sLogFileManager!!.writeLogToFile(logMsg)
    }

    private fun formatLog(tag: String, msg: String?): String {
        return String.format(
            Locale.CHINA,
            "%s pid=%d %s; %s\n",
            LOG_DATE_TIME_FORMAT.format(Date()),
            Process.myPid(),
            tag,
            msg
        )
    }

    /**
     * Log中的常量是int值，不适合给外面使用，这里统一用这个枚举值进行设置
     */
    enum class LogLevel(val value: Int) {
        VERBOSE(Log.VERBOSE), DEBUG(Log.DEBUG), INFO(Log.INFO), WARN(Log.WARN), ERROR(Log.ERROR), ASSERT(
            Log.ASSERT
        );

    }

    class LogFileManager internal constructor(private val mLogFileDir: String) {
        private var mCurrentLogFile: File? = null

        /**
         * 将log写入文件
         * @param logMessage
         */
        fun writeLogToFile(logMessage: String?) {
            if (mCurrentLogFile == null || mCurrentLogFile!!.length() >= LOG_FILE_MAX_SIZE) {
                mCurrentLogFile = newLogFile
            }
            FileUtil.writeToFile(logMessage, mCurrentLogFile!!.path)
        }// 删掉最老的文件// 创建新文件

        /**
         * 获取要写入的文件
         * @return
         */
        private val newLogFile: File
            get() {
                val dir = File(mLogFileDir)
                val files = dir.listFiles(fileFilter)
                if (files == null || files.size == 0) {
                    // 创建新文件
                    return createNewLogFileIfNeed()
                }
                val sortedFiles = sortFiles(files)
                if (files.size > LOG_FILES_MAX_NUM) {
                    // 删掉最老的文件
                    FileUtil.delete(sortedFiles[0])
                }
                return createNewLogFileIfNeed()
            }

        /**
         * 创建文件
         * @return
         */
        private fun createNewLogFileIfNeed(): File {
            return FileUtil.createFile(
                mLogFileDir + File.separator + PREFIX + LOG_FILE_DATE_FORMAT.format(
                    Date()
                ) + ".txt"
            )!!
        }

        /**
         * 排序
         * @param files
         * @return
         */
        private fun sortFiles(files: Array<File>): List<File> {
            val fileList = Arrays.asList(*files)
            Collections.sort(fileList, FileComparator())
            return fileList
        }

        /**
         * 比较器
         */
        private inner class FileComparator : Comparator<File?> {
            override fun compare(p0: File?, p1: File?): Int {
                return if (p0?.lastModified()!! < p1?.lastModified()!!) {
                    -1
                } else {
                    1
                }
            }
        }

        private val fileFilter = FileFilter { file ->
            val tmp = file.name.lowercase(Locale.getDefault())
            if (tmp.startsWith("log") && tmp.endsWith(".txt")) {
                true
            } else false
        }

        companion object {
            private const val LOG_FILES_MAX_NUM = 5 //文件最多有5个
            private const val LOG_FILE_MAX_SIZE = 1000 * 1000 * 20 //文件最大20MB
            private val LOG_FILE_DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd")
            const val PREFIX = "Log"
        }
    }
}
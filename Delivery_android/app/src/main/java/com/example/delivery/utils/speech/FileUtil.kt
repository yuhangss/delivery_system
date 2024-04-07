package com.example.delivery.utils.speech

import android.text.TextUtils
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileWriter
import java.util.zip.GZIPOutputStream

object FileUtil {
    /**
     * 创建文件
     * @param path
     * @return
     */
    @Synchronized
    fun createFile(path: String?): File? {
        if (TextUtils.isEmpty(path)) {
            return null
        }
        val file = File(path)
        if (file.isFile) {
            return file
        }
        val parentFile = file.parentFile
        if (parentFile != null && (parentFile.isDirectory || parentFile.mkdirs())) {
            try {
                if (file.createNewFile()) {
                    return file
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
    }

    /**
     * 删除文件
     * @param path
     * @return
     */
    @Synchronized
    fun delete(path: File?): Boolean {
        if (null == path) {
            return true
        }
        if (path.isDirectory) {
            val files = path.listFiles()
            if (null != files) {
                for (file in files) {
                    if (!delete(file)) {
                        return false
                    }
                }
            }
        }
        return !path.exists() || path.delete()
    }

    /**
     * 写文件
     * @param content
     * @param filePath
     */
    fun writeToFile(content: String?, filePath: String?) {
        var fileWriter: FileWriter? = null
        try {
            fileWriter = FileWriter(filePath, true)
            fileWriter.write(content)
            fileWriter.flush()
        } catch (t: Throwable) {
            t.printStackTrace()
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    /**
     * 将图片转换为字节数组
     * @return
     */
    fun loadImage(file: File): ByteArray? {
        //用于返回的字节数组
        var data: ByteArray? = null
        //打开文件输入流
        var fin: FileInputStream? = null
        //打开字节输出流
        var bout: ByteArrayOutputStream? = null
        try {
            //文件输入流获取对应文件
            fin = FileInputStream(file)
            //输出流定义缓冲区大小
            bout = ByteArrayOutputStream(file.length().toInt())
            //定义字节数组，用于读取文件流
            val buffer = ByteArray(1024)
            //用于表示读取的位置
            var len = -1
            //开始读取文件
            while (fin.read(buffer).also { len = it } != -1) {
                //从buffer的第0位置开始，读取至第len位置，结果写入bout
                bout.write(buffer, 0, len)
            }
            //将输出流转为字节数组
            data = bout.toByteArray()
            //关闭输入输出流
            fin.close()
            bout.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return data
    }

    /**
     * 把字节数组转化为字符串----"ISO-8859-1"
     * @param data
     * @return
     */
    fun byteToString(data: ByteArray?): String? {
        var dataString: String? = null
        try {
            //将字节数组转为字符串，编码格式为ISO-8859-1
            dataString = String(data!!, charset("ISO-8859-1"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return dataString
    }

    /**
     * 压缩字符串----"ISO-8859-1"
     * @param data
     * @return
     */
    fun compress(data: String): String? {
        var finalData: String? = null
        try {
            //打开字节输出流
            val bout = ByteArrayOutputStream()
            //打开压缩用的输出流,压缩后的结果放在bout中
            val gout = GZIPOutputStream(bout)
            //写入待压缩的字节数组
            gout.write(data.toByteArray(charset("ISO-8859-1")))
            //完成压缩写入
            gout.finish()
            //关闭输出流
            gout.close()
            finalData = bout.toString("ISO-8859-1")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return finalData
    }
}
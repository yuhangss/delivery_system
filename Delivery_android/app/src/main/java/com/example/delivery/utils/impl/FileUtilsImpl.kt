package com.example.delivery.utils.impl

import android.content.Context
import android.os.Environment
import com.example.delivery.utils.FileUtils
import java.io.File

class FileUtilsImpl: FileUtils{
    companion object {
        private const val IMAGE_PREFIX = "image"
        private const val JPG_SUFFIX = ".jpg"
        private const val FOLDER_NAME = "photo"
    }

    override fun createDirectoryNotExits(context: Context) {
        val folder = File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath +
                    File.separator + FOLDER_NAME
        )
        if(!folder.exists()) {
            folder.mkdirs()
        }
    }

    override fun createFile(context: Context)= File (
        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath +
                File.separator + FOLDER_NAME + File.separator + IMAGE_PREFIX +
                System.currentTimeMillis() + JPG_SUFFIX
    )
}
package com.example.delivery.utils

import android.content.Context
import java.io.File

interface FileUtils {
    fun createDirectoryNotExits(context: Context)
    fun createFile(context: Context): File
}
package com.example.delivery.ui.screens

import android.R
import android.util.Log
import android.widget.TextView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.io.BufferedReader
import java.io.InputStreamReader


@Composable

fun LogcatView() {
    Log.i("Logcat", "LogcatView")
    val process = Runtime.getRuntime().exec("logcat -d")
    val bufferedReader = BufferedReader(
        InputStreamReader(process.inputStream)
    )
    val log by remember {
        mutableStateOf(StringBuilder())
    }
    var line: String?
    while (bufferedReader.readLine().also {
            line = it
        } != null
    ) {
        log.append(line + "\n")
    }
    Column(
        modifier = Modifier.verticalScroll(
            state = rememberScrollState(),
            enabled = true,
            reverseScrolling = true),
    ) {
        Text(
            text = log.toString(),
            modifier = Modifier.padding(10.dp)
        )
    }
}
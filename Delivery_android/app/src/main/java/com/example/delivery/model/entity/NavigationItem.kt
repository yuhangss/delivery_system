package com.example.delivery.model.entity

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * 导航栏对象
 * @property title 导航栏标题
 * @property ImageVector 导航栏图标
 */
data class NavigationItem(
    val title: String,
    val icon: ImageVector
)

package com.empty.android.core.model

import kotlinx.serialization.Serializable

/**
 * Demo 数据实体：演示一个业务模型在整个项目中的流转。
 * 真实业务可以替换成自己的数据模型。
 */
@Serializable
data class DemoItem(
    val id: String,
    val title: String,
    val description: String,
    val iconUrl: String? = null,
)

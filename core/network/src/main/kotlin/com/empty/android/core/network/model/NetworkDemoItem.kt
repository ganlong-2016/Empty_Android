package com.empty.android.core.network.model

import com.empty.android.core.model.DemoItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkDemoItem(
    @SerialName("id") val id: String,
    @SerialName("title") val title: String,
    @SerialName("description") val description: String = "",
    @SerialName("iconUrl") val iconUrl: String? = null,
)

fun NetworkDemoItem.asExternalModel(): DemoItem = DemoItem(
    id = id,
    title = title,
    description = description,
    iconUrl = iconUrl,
)

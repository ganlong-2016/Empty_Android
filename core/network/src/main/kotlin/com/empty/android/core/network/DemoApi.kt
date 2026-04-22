package com.empty.android.core.network

import com.empty.android.core.network.model.NetworkDemoItem
import retrofit2.http.GET

/**
 * Retrofit API 示例：后续可按业务替换成真实接口。
 */
interface DemoApi {

    @GET("demo/items")
    suspend fun getDemoItems(): List<NetworkDemoItem>
}

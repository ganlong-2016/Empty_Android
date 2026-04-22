package com.empty.android.core.common.network

import javax.inject.Qualifier

/**
 * 用于注入与 Application 生命周期绑定的 [kotlinx.coroutines.CoroutineScope]。
 */
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ApplicationScope

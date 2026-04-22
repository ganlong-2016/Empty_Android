package com.empty.android.core.common.network

import javax.inject.Qualifier

/**
 * 统一的协程调度器 qualifier，业务代码通过注入获取具体的 [kotlinx.coroutines.CoroutineDispatcher]。
 * 这样更方便在测试中替换为 TestDispatcher。
 */
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(val dispatcher: AppDispatcher)

enum class AppDispatcher {
    Default,
    IO,
    Main,
}

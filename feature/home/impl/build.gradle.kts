plugins {
    alias(libs.plugins.emptyandroid.android.feature.impl)
}

android {
    namespace = "com.empty.android.feature.home.impl"
}

dependencies {
    implementation(projects.feature.home.api)
    // 示例：impl 需要跨 feature 跳转时，依赖目标 feature 的 api（而不是 impl）
    implementation(projects.feature.settings.api)
}

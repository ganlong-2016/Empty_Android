plugins {
    alias(libs.plugins.scania.android.feature.impl)
}

android {
    namespace = "com.scania.android.feature.launcher.impl"
}

dependencies {
    implementation(projects.feature.launcher.api)

    // Launcher 需要消费车机数据
    implementation(projects.core.car)

    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material.iconsExtended)
}

plugins {
    alias(libs.plugins.scania.android.library)
    alias(libs.plugins.scania.android.library.compose)
}

android {
    namespace = "com.scania.android.core.designsystem"
}

dependencies {
    api(libs.androidx.core.ktx)
    api(libs.coil.kt.compose)
}

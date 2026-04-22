plugins {
    alias(libs.plugins.emptyandroid.android.library)
    alias(libs.plugins.emptyandroid.android.library.compose)
}

android {
    namespace = "com.empty.android.core.designsystem"
}

dependencies {
    api(libs.androidx.core.ktx)
    api(libs.coil.kt.compose)
}

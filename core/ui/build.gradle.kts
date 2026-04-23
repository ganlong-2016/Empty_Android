plugins {
    alias(libs.plugins.scania.android.library)
    alias(libs.plugins.scania.android.library.compose)
}

android {
    namespace = "com.scania.android.core.ui"
}

dependencies {
    api(projects.core.designsystem)
    api(projects.core.model)

    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.coil.kt.compose)
}

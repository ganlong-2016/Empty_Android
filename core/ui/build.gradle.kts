plugins {
    alias(libs.plugins.emptyandroid.android.library)
    alias(libs.plugins.emptyandroid.android.library.compose)
}

android {
    namespace = "com.empty.android.core.ui"
}

dependencies {
    api(projects.core.designsystem)
    api(projects.core.model)

    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.coil.kt.compose)
}

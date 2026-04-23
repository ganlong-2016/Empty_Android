plugins {
    alias(libs.plugins.scania.android.library)
    alias(libs.plugins.scania.android.hilt)
}

android {
    namespace = "com.scania.android.core.domain"
}

dependencies {
    api(projects.core.data)
    api(projects.core.model)
    api(projects.core.common)

    implementation(libs.kotlinx.coroutines.android)
}

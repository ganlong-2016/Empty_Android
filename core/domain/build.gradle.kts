plugins {
    alias(libs.plugins.emptyandroid.android.library)
    alias(libs.plugins.emptyandroid.android.hilt)
}

android {
    namespace = "com.empty.android.core.domain"
}

dependencies {
    api(projects.core.data)
    api(projects.core.model)
    api(projects.core.common)

    implementation(libs.kotlinx.coroutines.android)
}

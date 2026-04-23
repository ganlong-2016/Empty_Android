plugins {
    alias(libs.plugins.scania.android.library)
    alias(libs.plugins.scania.android.hilt)
    alias(libs.plugins.scania.android.room)
}

android {
    namespace = "com.scania.android.core.database"
}

dependencies {
    api(projects.core.model)
    implementation(libs.kotlinx.coroutines.android)
}

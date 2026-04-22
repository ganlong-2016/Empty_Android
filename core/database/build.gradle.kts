plugins {
    alias(libs.plugins.emptyandroid.android.library)
    alias(libs.plugins.emptyandroid.android.hilt)
    alias(libs.plugins.emptyandroid.android.room)
}

android {
    namespace = "com.empty.android.core.database"
}

dependencies {
    api(projects.core.model)
    implementation(libs.kotlinx.coroutines.android)
}

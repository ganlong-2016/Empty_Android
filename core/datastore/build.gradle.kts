plugins {
    alias(libs.plugins.scania.android.library)
    alias(libs.plugins.scania.android.hilt)
}

android {
    namespace = "com.scania.android.core.datastore"
}

dependencies {
    api(projects.core.model)
    api(projects.core.common)

    implementation(libs.androidx.dataStore.preferences)
    implementation(libs.kotlinx.coroutines.android)
}

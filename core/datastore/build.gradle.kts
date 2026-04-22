plugins {
    alias(libs.plugins.emptyandroid.android.library)
    alias(libs.plugins.emptyandroid.android.hilt)
}

android {
    namespace = "com.empty.android.core.datastore"
}

dependencies {
    api(projects.core.model)
    api(projects.core.common)

    implementation(libs.androidx.dataStore.preferences)
    implementation(libs.kotlinx.coroutines.android)
}

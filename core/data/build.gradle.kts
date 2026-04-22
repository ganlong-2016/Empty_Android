plugins {
    alias(libs.plugins.emptyandroid.android.library)
    alias(libs.plugins.emptyandroid.android.hilt)
}

android {
    namespace = "com.empty.android.core.data"
}

dependencies {
    api(projects.core.model)
    api(projects.core.common)
    api(projects.core.database)
    api(projects.core.datastore)
    api(projects.core.network)

    implementation(libs.kotlinx.coroutines.android)
}

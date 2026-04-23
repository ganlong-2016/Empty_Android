plugins {
    alias(libs.plugins.scania.android.library)
    alias(libs.plugins.scania.android.hilt)
}

android {
    namespace = "com.scania.android.core.data"
}

dependencies {
    api(projects.core.model)
    api(projects.core.common)
    api(projects.core.database)
    api(projects.core.datastore)
    api(projects.core.network)

    implementation(libs.kotlinx.coroutines.android)
}

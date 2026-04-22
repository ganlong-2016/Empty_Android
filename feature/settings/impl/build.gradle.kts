plugins {
    alias(libs.plugins.emptyandroid.android.feature.impl)
}

android {
    namespace = "com.empty.android.feature.settings.impl"
}

dependencies {
    implementation(projects.feature.settings.api)
}

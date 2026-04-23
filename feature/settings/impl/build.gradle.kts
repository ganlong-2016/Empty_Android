plugins {
    alias(libs.plugins.scania.android.feature.impl)
}

android {
    namespace = "com.scania.android.feature.settings.impl"
}

dependencies {
    implementation(projects.feature.settings.api)
}

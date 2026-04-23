plugins {
    alias(libs.plugins.emptyandroid.android.feature.impl)
}

android {
    namespace = "com.empty.android.feature.landing.impl"
}

dependencies {
    implementation(projects.feature.landing.api)
    implementation(projects.feature.home.api)
}

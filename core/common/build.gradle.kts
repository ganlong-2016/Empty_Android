plugins {
    alias(libs.plugins.scania.android.library)
    alias(libs.plugins.scania.android.hilt)
}

android {
    namespace = "com.scania.android.core.common"
}

dependencies {
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.timber)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}

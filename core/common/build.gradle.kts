plugins {
    alias(libs.plugins.emptyandroid.android.library)
    alias(libs.plugins.emptyandroid.android.hilt)
}

android {
    namespace = "com.empty.android.core.common"
}

dependencies {
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.timber)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}

plugins {
    alias(libs.plugins.emptyandroid.android.application)
    alias(libs.plugins.emptyandroid.android.application.compose)
    alias(libs.plugins.emptyandroid.android.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.empty.android.app"

    defaultConfig {
        applicationId = "com.empty.android.app"
        versionCode = 1
        versionName = "1.0.0"
        targetSdk = 36
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables { useSupportLibrary = true }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            isDebuggable = true
        }
        release {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(projects.core.common)
    implementation(projects.core.designsystem)
    implementation(projects.core.ui)
    implementation(projects.core.data)
    implementation(projects.core.model)
    implementation(projects.core.domain)
    implementation(projects.core.datastore)
    implementation(projects.core.database)
    implementation(projects.core.network)

    // Feature impl 模块（真正的 UI + ViewModel）
    implementation(projects.feature.home.impl)
    implementation(projects.feature.settings.impl)
    // Feature api 模块（app 直接用到路由 key / 跳转函数时依赖）
    implementation(projects.feature.home.api)
    implementation(projects.feature.settings.api)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.timber)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext)
    androidTestImplementation(libs.androidx.test.espresso.core)
}

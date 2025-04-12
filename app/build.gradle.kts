plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.mainproject"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.mainproject"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.navigation.runtime.android)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.animation.core.lint)
    implementation(libs.androidx.espresso.core)
    implementation(libs.androidx.media3.common.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0") // Kiểm tra phiên bản mới nhất
    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.12.0"))


    // TODO: Add the dependencies for Firebase products you want to use
    // When using the BoM, don't specify versions in Firebase dependencies
    implementation("com.google.firebase:firebase-analytics")

    implementation("com.google.firebase:firebase-auth-ktx:22.0.0")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    // Thêm các SDK Firebase khác bạn muốn sử dụng (ví dụ: Realtime Database, Storage, etc.)
    implementation("com.google.firebase:firebase-database")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-messaging")


    implementation("com.google.android.gms:play-services-auth:20.5.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    implementation("androidx.compose.ui:ui:1.6.0")
    implementation("androidx.compose.foundation:foundation:1.6.0")
    implementation("androidx.compose.material3:material3:1.1.2")
    implementation("androidx.compose.material:material-icons-extended:1.5.4")
    implementation ("com.google.accompanist:accompanist-navigation-animation:0.36.0")
    implementation ("com.google.accompanist:accompanist-pager:0.24.13-rc")
    implementation("com.github.skydoves:landscapist-glide:2.4.7")
    implementation("com.github.skydoves:landscapist-coil:2.4.7")
    implementation("androidx.compose.material:material-icons-extended-android:1.7.8")
    implementation("androidx.compose.animation:animation:1.7.8")
    implementation("androidx.compose.foundation:foundation:1.7.8")
    implementation("androidx.compose.material:material:1.7.8")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.compose.material3:material3:1.3.1")
    implementation("androidx.compose.runtime:runtime:1.7.8")
    implementation("androidx.compose.runtime:runtime-livedata:1.7.8")
    implementation("androidx.compose.runtime:runtime-rxjava2:1.7.8")
    implementation("androidx.compose.ui:ui:1.7.8")
    implementation("androidx.concurrent:concurrent-futures-ktx:1.2.0")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.1.0-beta01")
    implementation("androidx.contentpager:contentpager:1.0.0")
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.2.0")
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.core:core-role:1.1.0")
    implementation("androidx.core:core-animation:1.0.0")
    androidTestImplementation("androidx.core:core-animation-testing:1.0.0")
    implementation("androidx.core:core-performance:1.0.0")
    implementation("androidx.core:core-google-shortcuts:1.1.0")
    implementation("androidx.core:core-remoteviews:1.1.0")
    implementation("androidx.core:core-splashscreen:1.2.0-beta01")
    implementation("androidx.core.uwb:uwb:1.0.0-alpha10")
    implementation("androidx.credentials:credentials:1.5.0")

}
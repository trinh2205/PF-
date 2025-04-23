plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.services)
    alias(libs.plugins.ksp)
//    alias(libs.plugins.hilt)
}

android {
    namespace = "com.example.mainproject"
    compileSdk = 35
    ndkVersion = "26.1.10909125"

    defaultConfig {
        applicationId = "com.example.mainproject"
//        applicationId = "com.example.mainproject.test"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.17"
    }
}

dependencies {
    // JavaPoet
//    implementation("com.squareup:javapoet:1.13.0")

    // Material
    implementation(libs.material)

    // AndroidX Core
    implementation(libs.androidx.core.ktx)
    implementation("androidx.core:core-splashscreen:1.0.1")

    // Jetpack Compose
    implementation(platform("androidx.compose:compose-bom:2024.10.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.animation:animation")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.runtime:runtime")
    implementation("androidx.compose.runtime:runtime-livedata")
    implementation("androidx.compose.runtime:runtime-rxjava2")
    implementation("androidx.compose.material:material")
    implementation("androidx.activity:activity-compose")
    implementation("androidx.navigation:navigation-compose")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.1.0")

    // Lifecycle
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Hilt
//    implementation("com.google.dagger:hilt-android:2.56.2")
//    ksp("com.google.dagger:hilt-compiler:2.56.2")
//    implementation("androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha03")
//    ksp("androidx.hilt:hilt-compiler:1.0.0")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.12.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-messaging")

    // Google Play Services
    implementation("com.google.android.gms:play-services-auth:21.2.0")

//    // Image Loading
//    implementation("com.github.skydoves:landscapist-coil:2.4.7") {
//        exclude(group = "com.squareup", module = "javapoet")
//    }
//    implementation("io.coil-kt:coil-compose:2.7.0") {
//        exclude(group = "com.squareup", module = "javapoet")
//    }
    implementation("com.github.skydoves:landscapist-coil:2.4.7")
    implementation("io.coil-kt:coil-compose:2.7.0")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.10.00"))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.0.21")
    implementation("org.jetbrains.kotlin:kotlin-reflect:2.0.21")
}

//configurations.all {
//    resolutionStrategy {
//        force("com.squareup:javapoet:1.13.0")
//    }
//}
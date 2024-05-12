plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id ("kotlin-kapt")
}

android {
    namespace = "com.example.esp_v3"
    compileSdk = 34

    kapt {
        generateStubs = true
    }

    defaultConfig {
        applicationId = "com.example.esp_v3"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        viewBinding = true
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.7")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.7")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.2.0-alpha01")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-savedstate:1.0.0-alpha01")
    implementation ("com.squareup.retrofit2:retrofit:2.11.0")
    implementation ("androidx.coordinatorlayout:coordinatorlayout:1.2.0")

    val dagger = "2.12"
    implementation ("com.google.dagger:dagger:${dagger}")
    annotationProcessor ("com.google.dagger:dagger-compiler:${dagger}")
    implementation ("com.google.dagger:dagger-android-support:${dagger}")
    annotationProcessor ("com.google.dagger:dagger-android-processor:${dagger}")
}
plugins {
    alias(libs.plugins.android.application)
    // Kotlin serialization plugin for type safe routes and navigation arguments
    kotlin("plugin.serialization") version "2.0.21"
    id("androidx.navigation.safeargs")
}

android {

    android {
        packaging {
            resources {
                pickFirsts.add ("mozilla/public-suffix-list.txt")
                excludes.add("META-INF/INDEX.LIST")
                excludes.add("META-INF/DEPENDENCIES")
                excludes.add("META-INF/LICENSE")
                excludes.add("META-INF/LICENSE.txt")
                excludes.add("META-INF/NOTICE")
                excludes.add("META-INF/NOTICE.txt")
                excludes.add("META-INF/io.netty.versions.properties")
            }
        }
    }


    namespace = "com.example.greenlens"
    compileSdk = 34



    defaultConfig {
        applicationId = "com.example.greenlens"
        minSdk = 24
        targetSdk = 34
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

    buildFeatures {
        dataBinding = true
        viewBinding = true
    }


}

dependencies {

    implementation ("com.google.guava:guava:31.1-android")

    implementation ("androidx.camera:camera-core:1.3.0-alpha04")
    implementation ("androidx.camera:camera-camera2:1.3.0-alpha04")
    implementation ("androidx.camera:camera-lifecycle:1.3.0-alpha04")
    implementation ("androidx.camera:camera-view:1.3.0-alpha04")

    implementation ("com.github.bumptech.glide:glide:4.12.0")
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    implementation(libs.firebase.crashlytics.buildtools)
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")
    implementation ("androidx.recyclerview:recyclerview:1.3.2")
    // Material Design 라이브러리
    implementation("com.google.android.material:material:1.11.0")

    // 안드로이드 기본 의존성
    implementation("androidx.appcompat:appcompat:1.6.1")

    // 뷰모델과 라이브데이터
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.6.1")
    implementation("androidx.lifecycle:lifecycle-livedata:2.6.1")

    // 카메라X
    implementation("androidx.camera:camera-camera2:1.2.2")
    implementation("androidx.camera:camera-lifecycle:1.2.2")
    implementation("androidx.camera:camera-view:1.2.2")


    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.firebase.appdistribution.gradle)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // navigation
    val nav_version = "2.8.8"

    // Jetpack Compose integration
    implementation("androidx.navigation:navigation-compose:$nav_version")

    // Views/Fragments integration
    implementation("androidx.navigation:navigation-fragment:$nav_version")
    implementation("androidx.navigation:navigation-ui:$nav_version")

    // Feature module support for Fragments
    implementation("androidx.navigation:navigation-dynamic-features-fragment:$nav_version")

    // Testing Navigation
    androidTestImplementation("androidx.navigation:navigation-testing:$nav_version")

    // JSON serialization library, works with the Kotlin serialization plugin
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")

    // Gson
    implementation("com.google.code.gson:gson:2.8.9")
}
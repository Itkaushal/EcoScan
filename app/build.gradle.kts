plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.kaushalprajapati.ecoscan"
    compileSdk {
        version = release(37) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.kaushalprajapati.ecoscan"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.googleid)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(libs.navigation.compose)

    implementation(libs.lifecycle.runtime)

    implementation(libs.lifecycle.compose)

    implementation(libs.viewmodel.compose)

    implementation(libs.coroutines)

    implementation(libs.room.runtime)

    implementation(libs.room.ktx)

    ksp(libs.room.compiler)

    implementation(libs.retrofit)

    implementation(libs.retrofit.gson)

    implementation(libs.okhttp)

    implementation(libs.logging)

    implementation(libs.coil)

    implementation(libs.camera.core)

    implementation(libs.camera.camera2)

    implementation(libs.camera.lifecycle)

    implementation(libs.camera.view)

    implementation(libs.barcode)

    implementation(libs.work)

    implementation(libs.datastore)

    implementation(platform(libs.firebase.bom))

    implementation(libs.firebase.auth)

    implementation(libs.firebase.firestore)

    implementation(libs.firebase.storage)

    implementation(libs.firebase.analytics)

    implementation(libs.material3)

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.10.2")

    // ML Kit Barcode
    implementation("com.google.mlkit:barcode-scanning:17.3.0")

// Accompanist Permission
    implementation("com.google.accompanist:accompanist-permissions:0.37.3")

}
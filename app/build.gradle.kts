import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.myecommerceapp"
    compileSdk = 35
    packaging {
        resources {
            excludes += "META-INF/versions/*/OSGI-INF/MANIFEST.MF"
        }

        defaultConfig {
            applicationId = "com.example.myecommerceapp"
            minSdk = 27
            targetSdk = 35
            versionCode = 1
            versionName = "1.0"

            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

            val localProperties = Properties().apply {
                val file = rootProject.file("local.properties")
                if (file.exists()) {
                    load(file.inputStream())
                }
            }

            buildConfigField(
                "String",
                "CLOUDINARY_CLOUD_NAME",
                localProperties.getProperty("CLOUDINARY_CLOUD_NAME")
            )
            buildConfigField(
                "String",
                "CLOUDINARY_API_KEY",
                localProperties.getProperty("CLOUDINARY_API_KEY")
            )
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
            viewBinding = true
            buildConfig = true
        }
        composeOptions {
            kotlinCompilerExtensionVersion = "1.5.1"
        }
    }

    dependencies {
        implementation(libs.androidx.core.ktx)
        implementation(libs.androidx.lifecycle.runtime.ktx)
        implementation(libs.androidx.activity.compose)
        implementation(libs.androidx.lifecycle.viewmodel.compose)
        implementation(platform(libs.androidx.compose.bom))
        implementation(libs.androidx.ui)
        implementation(libs.androidx.ui.graphics)
        implementation(libs.androidx.ui.tooling.preview)
        implementation(libs.androidx.material3)
        implementation(libs.hilt.android)
        implementation(libs.constraintlayout)
        kapt(libs.hilt.android.compiler)
        implementation(libs.androidx.navigation.compose)
        implementation(libs.androidx.hilt.navigation.compose)
        implementation(libs.androidx.hilt.work)
        kapt(libs.androidx.hilt.compiler)
        implementation(libs.cloudinary.android.core)
        implementation(libs.androidx.material.icons.extended)
        implementation(libs.glide)
        implementation(libs.androidx.appcompat)
        implementation(libs.material)
        implementation(libs.androidx.constraintlayout)
        implementation(libs.androidx.recyclerview)
        implementation(libs.androidx.recyclerview.selection)
        implementation(libs.androidx.room.runtime)
        implementation(libs.androidx.room.ktx)
        implementation(libs.foundation)
        implementation(libs.kotlinx.coroutines.android.v173)
        implementation(libs.kotlinx.coroutines.core.v173)
        implementation(libs.androidx.lifecycle.runtime.ktx.v262)
        implementation(libs.androidx.work.runtime.ktx.v290)

        implementation(project(":feature-cart"))
        implementation(project(":feature-auth"))
        implementation(project(":feature-product-list"))
        implementation(project(":feature-order-history"))
        implementation(project(":feature-profile"))
        implementation(project(":core:model"))
        implementation(project(":core:ui"))
        implementation(project(":data"))

        testImplementation(libs.junit)
        androidTestImplementation(libs.androidx.junit)
        androidTestImplementation(libs.androidx.espresso.core)
        androidTestImplementation(platform(libs.androidx.compose.bom))
        androidTestImplementation(libs.androidx.ui.test.junit4)
        debugImplementation(libs.androidx.ui.tooling)
        debugImplementation(libs.androidx.ui.test.manifest)
    }
}

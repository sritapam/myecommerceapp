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

        buildConfigField("String", "CLOUDINARY_CLOUD_NAME", localProperties.getProperty("CLOUDINARY_CLOUD_NAME"))
        buildConfigField("String", "CLOUDINARY_API_KEY", localProperties.getProperty("CLOUDINARY_API_KEY"))
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
    // Añadir composeOptions aquí si no está ya
    composeOptions {
        // ESTA VERSIÓN DEBE COINCIDIR CON LA VERSIÓN DEL COMPILADOR DE KOTLIN QUE USA TU COMPOSE BOM
        // Para compose-bom:2023.08.00, la versión común es 1.5.1
        // Si usas una BOM más reciente (ej. 2024.04.00), puede ser 1.5.11 o superior.
        // VERIFICA TU libs.versions.toml para la versión de compose-bom y busca la tabla de compatibilidad.
        kotlinCompilerExtensionVersion = "1.5.1" // <--- ¡IMPORTANTE!
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

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.hilt.work)
    kapt("androidx.hilt:hilt-compiler:1.2.0")

    implementation(libs.play.services.analytics.impl)
    implementation(libs.play.services.cast.tv) // Mantengo si lo necesitas

    // Cloudinary
    implementation(libs.cloudinary.android.core)

    // Material Icons
    implementation(libs.androidx.material.icons.extended)

    // Networking
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    implementation(libs.glide)

    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.recyclerview.selection)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.foundation)

    implementation(libs.androidx.work.runtime.ktx.v290)

    // Módulos de tu proyecto
    implementation(project(":feature-cart"))
    implementation(project(":feature-auth"))
    implementation(project(":feature-product-list"))
    implementation(project(":feature-order-history"))
    implementation(project(":feature-profile"))
    implementation(project(":core:model"))
    implementation(project(":core:ui"))
    implementation(project(":data"))

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

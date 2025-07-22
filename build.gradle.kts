// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("com.google.dagger.hilt.android") version "2.56.2" apply false
    id("com.google.devtools.ksp") version "2.0.21-1.0.27" apply false
    id ("org.jetbrains.kotlin.jvm") version "1.9.23" apply false
    alias(libs.plugins.android.library) apply false
    id("androidx.room") version "2.7.0" apply false
}
buildscript {
    dependencies {
        classpath(libs.hilt.android.gradle.plugin)
    }
}

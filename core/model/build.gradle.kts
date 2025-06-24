plugins {
    kotlin("jvm")
}

dependencies {

    testImplementation(libs.junit)
    compileOnly("javax.inject:javax.inject:1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9")

}
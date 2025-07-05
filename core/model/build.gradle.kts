plugins {
    kotlin("jvm")
}

dependencies {

    implementation(libs.protolite.well.known.types)
    testImplementation(libs.junit)
    compileOnly(libs.javax.inject)
    implementation(libs.kotlinx.coroutines.android.v139)

}
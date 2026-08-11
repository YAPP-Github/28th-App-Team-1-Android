plugins {
    alias(libs.plugins.dminus14.jvm.library)
    alias(libs.plugins.dminus14.kotlin.quality)
}

dependencies {
    implementation(libs.javax.inject)
    implementation(libs.kotlinx.coroutines.core)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}

plugins {
    alias(libs.plugins.dminus14.jvm.library)
    alias(libs.plugins.dminus14.kotlin.quality)
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    implementation(libs.androidx.navigation3.runtime)
}

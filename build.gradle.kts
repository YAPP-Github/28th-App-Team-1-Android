// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // Default
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false


    // Hilt
    alias(libs.plugins.kotlin.ksp) apply false
    alias(libs.plugins.dagger.hilt.android) apply false
}
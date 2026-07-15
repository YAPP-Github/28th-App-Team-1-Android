plugins {
    alias(libs.plugins.dminus14.android.library)
    alias(libs.plugins.dminus14.android.lint)
}

android {
    namespace = "com.dminus14.app.core.common"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.mockk.android)
    androidTestImplementation(libs.pdfbox.android)
}

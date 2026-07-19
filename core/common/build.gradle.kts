plugins {
    alias(libs.plugins.dminus14.android.library)
    alias(libs.plugins.dminus14.android.test)
    alias(libs.plugins.dminus14.android.quality)
}

android {
    namespace = "com.dminus14.app.core.common"
}

dependencies {
    androidTestImplementation(libs.mockk.android)
    androidTestImplementation(libs.pdfbox.android)
}

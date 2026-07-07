plugins {
    alias(libs.plugins.dminus14.android.library)
    alias(libs.plugins.dminus14.android.hilt)
    alias(libs.plugins.dminus14.android.network)
    alias(libs.plugins.dminus14.android.lint)
}

android {
    namespace = "com.dminus14.app.data"
}

plugins {
    alias(libs.plugins.dminus14.android.feature)
}

android {
    namespace = "com.dminus14.app.feature.main"
}

dependencies {
    implementation(project(":feature:main:api"))
}

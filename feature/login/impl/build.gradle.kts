plugins {
    alias(libs.plugins.dminus14.android.feature)
}

android {
    namespace = "com.dminus14.app.feature.login"
}

dependencies {
    api(project(":feature:login:api"))
    implementation(project(":domain"))
    implementation(project(":feature:main:api"))
}

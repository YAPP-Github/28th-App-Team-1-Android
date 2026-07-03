plugins {
    alias(libs.plugins.dminus14.android.application)
    alias(libs.plugins.dminus14.android.lint)
}

dependencies {
    implementation(project(":feature:main:api"))
    implementation(project(":feature:main:impl"))
}

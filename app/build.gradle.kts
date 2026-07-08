plugins {
    alias(libs.plugins.dminus14.android.application)
    alias(libs.plugins.dminus14.android.lint)
}

dependencies {
    implementation(project(":feature:login:impl"))
    implementation(project(":feature:main:impl"))
}

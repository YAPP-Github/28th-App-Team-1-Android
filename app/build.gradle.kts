plugins {
    alias(libs.plugins.dminus14.android.application)
    alias(libs.plugins.dminus14.android.lint)
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":designsystem"))
    implementation(project(":feature:main:impl"))
    implementation(libs.androidx.lifecycle.runtime.compose)

    testImplementation(libs.kotlinx.coroutines.test)
}

plugins {
    alias(libs.plugins.dminus14.jvm.library)
    alias(libs.plugins.dminus14.kotlin.quality)
}

dependencies {
    implementation(project(":catalog:annotations"))
    implementation(libs.ksp.symbol.processing.api)
    implementation(libs.kotlinpoet.ksp)

    testImplementation(libs.junit)
    testImplementation(libs.kctfork.ksp)
}

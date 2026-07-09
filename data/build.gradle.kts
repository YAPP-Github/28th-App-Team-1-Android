import java.util.Properties

plugins {
    alias(libs.plugins.dminus14.android.library)
    alias(libs.plugins.dminus14.android.hilt)
    alias(libs.plugins.dminus14.android.network)
    alias(libs.plugins.dminus14.android.datastore)
    alias(libs.plugins.dminus14.android.lint)
}

val localProperties =
    Properties().apply {
        val file = rootProject.file("local.properties")
        if (file.exists()) {
            file.inputStream().use(::load)
        }
    }

val serverUrl = localProperties.getProperty("SERVER_URL").orEmpty()

android {
    namespace = "com.dminus14.app.data"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        buildConfigField("String", "SERVER_URL", "\"$serverUrl\"")
    }
}

dependencies {
    implementation(project(":domain"))
    implementation(libs.kakao.v2.user)
}

import java.util.Properties

plugins {
    alias(libs.plugins.dminus14.android.application)
    alias(libs.plugins.dminus14.android.lint)
}

val localProperties =
    Properties().apply {
        val file = rootProject.file("local.properties")
        if (file.exists()) {
            file.inputStream().use(::load)
        }
    }

val kakaoNativeAppKey = localProperties.getProperty("KAKAO_NATIVE_APP_KEY").orEmpty()
val serverUrl = localProperties.getProperty("SERVER_URL").orEmpty()

android {
    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        buildConfigField("String", "KAKAO_NATIVE_APP_KEY", "\"$kakaoNativeAppKey\"")
        manifestPlaceholders["KAKAO_NATIVE_APP_KEY"] = kakaoNativeAppKey

        buildConfigField("String", "SERVER_URL", "\"$serverUrl\"")
    }
}

dependencies {
    implementation(libs.kakao.v2.common)
    implementation(project(":data"))
    implementation(project(":feature:login:impl"))
    implementation(project(":feature:main:impl"))
}

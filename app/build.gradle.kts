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

android {
    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        buildConfigField("String", "KAKAO_NATIVE_APP_KEY", "\"$kakaoNativeAppKey\"")
        manifestPlaceholders["KAKAO_NATIVE_APP_KEY"] = kakaoNativeAppKey
    }
}

dependencies {
    implementation(libs.kakao.user)
    implementation(project(":feature:login:impl"))
    implementation(project(":feature:main:impl"))
}

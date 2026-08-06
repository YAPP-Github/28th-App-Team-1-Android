import java.util.Properties

plugins {
    alias(libs.plugins.dminus14.android.application)
    alias(libs.plugins.dminus14.android.compose)
    alias(libs.plugins.dminus14.compose.preview)
    alias(libs.plugins.dminus14.compose.resources)
    alias(libs.plugins.dminus14.android.hilt)
    alias(libs.plugins.dminus14.android.navigation3)
    alias(libs.plugins.dminus14.android.test)
    alias(libs.plugins.dminus14.android.compose.test)
    alias(libs.plugins.dminus14.android.quality)
    alias(libs.plugins.dminus14.android.compose.lint)
}

android {
    lint {
        disable.add("MissingClass")
    }
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
    implementation(libs.kakao.v2.common)
    implementation(project(":data"))
    implementation(project(":feature:login:impl"))
    implementation(project(":feature:home:impl"))
    implementation(project(":feature:feedback:impl"))
    implementation(project(":core:common"))
    implementation(project(":designsystem"))
    implementation(project(":feature:main:impl"))
    implementation(project(":feature:mypage:impl"))
    implementation(project(":feature:onboarding:impl"))
    implementation(project(":feature:interview:impl"))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    testImplementation(libs.kotlinx.coroutines.test)
}

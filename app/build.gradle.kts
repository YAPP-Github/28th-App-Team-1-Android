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

val kakaoNativeAppKeyDebug = localProperties.getProperty("KAKAO_NATIVE_APP_KEY_DEBUG").orEmpty()
val kakaoNativeAppKeyRelease = localProperties.getProperty("KAKAO_NATIVE_APP_KEY_RELEASE").orEmpty()
val chottuLinkApiKey = localProperties.getProperty("CHOTTULINK_API_KEY").orEmpty()

android {
    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        buildConfigField("String", "KAKAO_NATIVE_APP_KEY", "\"$kakaoNativeAppKeyDebug\"")
        manifestPlaceholders["KAKAO_NATIVE_APP_KEY"] = kakaoNativeAppKeyDebug
        buildConfigField("String", "CHOTTULINK_API_KEY", "\"$chottuLinkApiKey\"")
    }

    buildTypes {
        release {
            buildConfigField("String", "KAKAO_NATIVE_APP_KEY", "\"$kakaoNativeAppKeyRelease\"")
            manifestPlaceholders["KAKAO_NATIVE_APP_KEY"] = kakaoNativeAppKeyRelease
        }
    }
}

dependencies {
    implementation(libs.kakao.v2.common)
    implementation(libs.chottulink.android.sdk)
    implementation(project(":data"))
    implementation(project(":domain"))
    implementation(project(":feature:login:impl"))
    implementation(project(":feature:home:impl"))
    implementation(project(":feature:feedback:impl"))
    implementation(project(":core:common"))
    implementation(project(":designsystem"))
    implementation(project(":feature:mypage:impl"))
    implementation(project(":feature:onboarding:impl"))
    implementation(project(":feature:interview:impl"))
    implementation(project(":feature:interviewreport:impl"))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.work)
    implementation(libs.okhttp)

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.androidx.work.testing)
}

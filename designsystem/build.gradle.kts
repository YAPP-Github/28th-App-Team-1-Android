import org.jetbrains.compose.resources.ResourcesExtension

plugins {
    alias(libs.plugins.dminus14.compose.multiplatform.ui.library)
    alias(libs.plugins.dminus14.compose.preview)
    alias(libs.plugins.dminus14.compose.resources)
    alias(libs.plugins.dminus14.kotlin.quality)
}

kotlin {
    android {
        namespace = "com.dminus14.app.designsystem"
    }
}

compose {
    resources {
        generateResClass = ResourcesExtension.ResourceClassGeneration.Never
    }
}

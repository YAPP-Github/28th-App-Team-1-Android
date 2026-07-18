import org.jetbrains.compose.resources.ResourcesExtension

plugins {
    alias(libs.plugins.dminus14.compose.multiplatform.library)
    alias(libs.plugins.dminus14.kotlin.quality)
}

kotlin {
    android {
        namespace = "com.dminus14.app.core.resources"
        androidResources.enable = true
    }

    sourceSets {
        commonMain {
            dependencies {
                api(libs.compose.runtime)
                api(libs.compose.components.resources)
            }
        }
    }
}

compose {
    resources {
        publicResClass = true
        packageOfResClass = "com.dminus14.app.core.resources"
        generateResClass = ResourcesExtension.ResourceClassGeneration.Always
    }
}

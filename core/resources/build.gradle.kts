import org.jetbrains.compose.resources.ResourcesExtension

plugins {
    alias(libs.plugins.dminus14.compose.multiplatform.library)
}

kotlin {
    android {
        namespace = "com.dminus14.app.core.resources"
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.compose.runtime)
                implementation(libs.compose.components.resources)
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

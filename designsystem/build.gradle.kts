import org.jetbrains.compose.resources.ResourcesExtension

plugins {
    alias(libs.plugins.dminus14.compose.multiplatform.common)
}

kotlin {
    android {
        namespace = "com.dminus14.app.designsystem"
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":core:resources"))
            }
        }
    }
}

compose {
    resources {
        generateResClass = ResourcesExtension.ResourceClassGeneration.Never
    }
}

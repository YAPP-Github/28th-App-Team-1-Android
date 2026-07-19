import org.jetbrains.compose.resources.ResourcesExtension

plugins {
    alias(libs.plugins.dminus14.compose.multiplatform.wasm.application)
    alias(libs.plugins.dminus14.kotlin.quality)
}

kotlin {
    sourceSets {
        wasmJsMain {
            dependencies {
                implementation(project(":designsystem"))
            }
        }
    }
}

// Prevent other modules to access on Catalog's resources
compose {
    resources {
        publicResClass = false
        packageOfResClass = "com.dminus14.catalog.generated.resources"
        generateResClass = ResourcesExtension.ResourceClassGeneration.Always
    }
}

import org.jetbrains.compose.resources.ResourcesExtension

plugins {
    alias(libs.plugins.dminus14.compose.multiplatform.wasm.application)
    alias(libs.plugins.dminus14.kotlin.quality)
    alias(libs.plugins.kotlin.ksp)
}

kotlin {
    sourceSets {
        wasmJsMain {
            dependencies {
                implementation(project(":designsystem"))
                implementation(project(":catalog:annotations"))
            }
        }
        wasmJsTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.compose.ui.test)
            }
        }
    }
}

dependencies {
    add("kspWasmJs", project(":catalog:processor"))
}

// Prevent other modules to access on Catalog's resources
compose {
    resources {
        publicResClass = false
        packageOfResClass = "com.dminus14.catalog.generated.resources"
        generateResClass = ResourcesExtension.ResourceClassGeneration.Always
    }
}

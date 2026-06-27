// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.ksp) apply false
    alias(libs.plugins.dagger.hilt.android) apply false
}

// Apply Spotless formatting on every commit
tasks.register("installGitHooks") {
    // Task description
    group = "git"
    description = "Configures Git to use hooks from the script directory."

    // Apply
    doLast {
        // Find file
        val hookFile = layout.projectDirectory.file("script/pre-commit").asFile
        require(hookFile.exists()) {
            "Missing Git hook: ${hookFile.path}"
        }

        // Grant execution permission and configure git hooks path
        hookFile.setExecutable(true)
        providers
            .exec {
                // Change default Git hook directory to './script'
                commandLine("git", "config", "core.hooksPath", "script")
            }.result
            .get()
            .assertNormalExitValue()
    }
}

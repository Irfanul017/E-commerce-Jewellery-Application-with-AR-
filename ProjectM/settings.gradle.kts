pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal() // This is required for resolving Gradle plugins
    }
}

dependencyResolutionManagement {
    // This mode ensures that repositories are defined only here and not in individual project files
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)

    repositories {
        maven { url = uri("https://jitpack.io") }
        google() // Google's Maven repository for Android dependencies
        mavenCentral() // Maven Central repository for most dependencies
      // JitPack for third-party libraries
    }
}

rootProject.name = "Major Project"
include(":app")

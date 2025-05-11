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
        gradlePluginPortal()
    }

    plugins {
        id("com.android.application") version "8.7.3" apply false
//        id("com.google.dagger.hilt.android") version "2.56.2" apply false
        id("org.jetbrains.kotlin.android") version "2.1.20" apply false
        id("com.google.gms.google-services") version "4.4.2" apply false
        id("com.google.devtools.ksp") version "2.0.21-1.0.25" apply false
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "MainProject"
include(":app")

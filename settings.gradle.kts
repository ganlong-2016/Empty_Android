pluginManagement {
    includeBuild("build-logic")
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
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "EmptyAndroid"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":app")

// Core modules
include(":core:common")
include(":core:model")
include(":core:designsystem")
include(":core:ui")
include(":core:data")
include(":core:database")
include(":core:datastore")
include(":core:network")
include(":core:domain")

// Feature modules (api + impl 分离，参考 nowinandroid)
include(":feature:home:api")
include(":feature:home:impl")
include(":feature:settings:api")
include(":feature:settings:impl")

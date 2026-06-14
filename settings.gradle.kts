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
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://artifactory-external.vkpartner.ru/artifactory/maven")
        }
        maven {
            url = uri("https://artifactory-external.vkpartner.ru/artifactory/maven-rustore-exposed/")
        }
    }
}

rootProject.name = "LoanCalcAndroid"
include(":app")
include(":calculation")
include(":core:database")
include(":core:data")

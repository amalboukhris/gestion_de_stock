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
        flatDir {
            dirs("libs")
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {

        maven {
            url = uri("https://zebratech.jfrog.io/artifactory/EMDK-Android/")
            credentials {
                username = "zebra_developer"
                password = "zebra_developer"
            }
        }



        google()
        mavenCentral()
    }
}

rootProject.name = "PDA"
include(":app")

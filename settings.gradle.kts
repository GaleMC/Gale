pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

rootProject.name = "gale" // Gale - build changes

include("gale-api", "gale-server") // Gale - build changes

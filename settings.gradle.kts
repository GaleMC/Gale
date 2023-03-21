import java.util.Locale

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.4.0"
}

if (!file(".git").exists()) {
    val errorText = """
        
        =====================[ ERROR ]=====================
         The Paper project directory is not a properly cloned Git repository.
         
         In order to build Paper from source you must clone
         the Paper repository using Git, not download a code
         zip from GitHub.
         
         Built Paper jars are available for download at
         https://papermc.io/downloads
         
         See https://github.com/PaperMC/Paper/blob/master/CONTRIBUTING.md
         for further information on building and modifying Paper.
        ===================================================
    """.trimIndent()
    error(errorText)
}

rootProject.name = "gale" // Gale - build changes

for (name in listOf("Gale-API", "Gale-Server")) { // Gale - build changes
    val projName = name.toLowerCase(Locale.ENGLISH)
    include(projName)
    findProject(":$projName")!!.projectDir = file(name)
}
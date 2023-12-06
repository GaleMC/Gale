import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import io.papermc.paperweight.util.path
import org.gradle.configurationcache.extensions.capitalized
import kotlin.io.path.deleteRecursively

plugins {
    java
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
    id("io.papermc.paperweight.patcher") version "1.5.10"
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }
}

val paperMavenPublicUrl = "https://repo.papermc.io/repository/maven-public/"

subprojects {
    tasks.withType<JavaCompile>().configureEach {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }
    tasks.withType<Javadoc> {
        options.encoding = Charsets.UTF_8.name()
    }
    tasks.withType<ProcessResources> {
        filteringCharset = Charsets.UTF_8.name()
    }
    tasks.withType<Test> {
        testLogging {
            showStackTraces = true
            exceptionFormat = TestExceptionFormat.FULL
            events(TestLogEvent.STANDARD_OUT)
        }
    }

    repositories {
        mavenCentral()
        maven(paperMavenPublicUrl)
    }
}

repositories {
    mavenCentral()
    maven(paperMavenPublicUrl) {
        content {
            onlyForConfigurations(configurations.paperclip.name)
        }
    }
}

dependencies {
    remapper("net.fabricmc:tiny-remapper:0.8.10:fat")
    decompiler("net.minecraftforge:forgeflower:2.0.627.2")
    paperclip("io.papermc:paperclip:3.0.3")
}

paperweight {
    serverProject.set(project(":gale-server")) // Gale - build changes

    remapRepo.set(paperMavenPublicUrl)
    decompileRepo.set(paperMavenPublicUrl)

    usePaperUpstream(providers.gradleProperty("paperRef")) {
        withPaperPatcher {
            apiPatchDir.set(layout.projectDirectory.dir("patches/api"))
            apiOutputDir.set(layout.projectDirectory.dir("gale-api")) // Gale - build changes

            serverPatchDir.set(layout.projectDirectory.dir("patches/server"))
            serverOutputDir.set(layout.projectDirectory.dir("gale-server")) // Gale - build changes
        }
    }
}

// Uncomment while updating for a new Minecraft version
//tasks.withType<io.papermc.paperweight.tasks.CollectATsFromPatches> {
//    extraPatchDir.set(layout.projectDirectory.dir("patches/unapplied/server"))
//}
// tasks.withType<io.papermc.paperweight.tasks.RebuildGitPatches> {
//     filterPatches.set(false)
// }

tasks.register("printMinecraftVersion") {
    doLast {
        println(providers.gradleProperty("mcVersion").get().trim())
    }
}

tasks.register("printGaleVersion") { // Gale - branding changes
    doLast {
        println(project.version)
    }
}

// Gale start - branding changes - package license into jar
for (classifier in arrayOf("mojmap", "reobf")) {
    // Based on io.papermc.paperweight.taskcontainers.BundlerJarTasks
    tasks.named("create${classifier.capitalized()}PaperclipJar") {
        doLast {

            // Based on io.papermc.paperweight.taskcontainers.BundlerJarTasks
            val jarName = listOfNotNull(
                project.name,
                "paperclip",
                project.version,
                classifier
            ).joinToString("-") + ".jar"

            // Based on io.papermc.paperweight.taskcontainers.BundlerJarTasks
            val zipFile = layout.buildDirectory.file("libs/$jarName").path

            val rootDir = io.papermc.paperweight.util.findOutputDir(zipFile)

            try {
                io.papermc.paperweight.util.unzip(zipFile, rootDir)

                val licenseFileName = "LICENSE.txt"
                project(":gale-server").projectDir.resolve(licenseFileName).copyTo(rootDir.resolve(licenseFileName).toFile())

                io.papermc.paperweight.util.ensureDeleted(zipFile)

                io.papermc.paperweight.util.zip(rootDir, zipFile)
            } finally {
                @OptIn(kotlin.io.path.ExperimentalPathApi::class)
                rootDir.deleteRecursively()
            }

        }
    }
}
// Gale end - branding changes - package license into jar

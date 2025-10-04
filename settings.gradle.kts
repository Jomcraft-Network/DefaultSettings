pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()

        maven("https://maven.isxander.dev/releases/")

        maven("https://maven.fabricmc.net/")

        maven("https://maven.neoforged.net/releases/")

        maven("https://maven.kikugie.dev/releases")
        maven("https://maven.kikugie.dev/snapshots")

        maven("https://maven.isxander.dev/releases")
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.7+"
}

stonecutter {
    kotlinController = true
    centralScript = "build.gradle.kts"

    create(rootProject) {
        fun mc(mcVersion: String, name: String = mcVersion, loaders: Iterable<String>) =
            loaders.forEach { vers("$name-$it", mcVersion) }

		mc("1.21.9", loaders = listOf("fabric", "neoforge"))
        mc("1.21.4", loaders = listOf("fabric", "neoforge"))
		mc("1.21", loaders = listOf("fabric", "neoforge"))
		mc("1.20.6", loaders = listOf("fabric"))
        mc("1.20.1", loaders = listOf("fabric", "forge"))
		mc("1.20", loaders = listOf("fabric", "forge"))
        mc("1.19.4", loaders = listOf("fabric", "forge"))
        mc("1.18.2", loaders = listOf("fabric", "forge"))
        mc("1.17.1", loaders = listOf("fabric", "forge"))

        vcsVersion = "1.21.4-fabric"
    }
}

rootProject.name = "DefaultSettings"


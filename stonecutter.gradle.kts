plugins {
    id("dev.kikugie.stonecutter")
}
stonecutter active "1.17.1-forge"

allprojects {
    repositories {
        mavenCentral()
        mavenLocal()
        maven("https://maven.neoforged.net/releases")
        maven("https://maven.fabricmc.net/")
        maven("https://cursemaven.com")
    }
}
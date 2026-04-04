@file:Suppress("LocalVariableName")

pluginManagement {
    repositories {
        maven {
            name = "Fabric"
            url = uri("https://maven.fabricmc.net/")
        }
        maven {
            name = "NeoForged"
            url = uri("https://maven.neoforged.net/releases")
        }
        maven {
            name = "RelativityMC"
            url = uri("https://repo.codemc.io/repository/relativitymc/")
        }
        gradlePluginPortal()
    }

    val loom_version: String by extra
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id.startsWith("org.relativitymc.neo-loom")) {
                useVersion(loom_version)
            }
        }
    }
}

include("fabric")
include("neoforge")

rootProject.name = "Nostalgic-Tweaks"

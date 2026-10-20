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

    val loom = extra["loom_version"] as String
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id.startsWith("org.relativitymc.neo-loom")) {
                useVersion(loom)
            }
        }
    }
}

include("fabric")
//include("neoforge")

rootProject.name = "Nostalgic-Tweaks"

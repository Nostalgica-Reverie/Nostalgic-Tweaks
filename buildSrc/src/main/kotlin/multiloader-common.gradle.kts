plugins {
    id("java")
    id("idea")
    id("java-library")
}

version = "${rootProject.mod.version}+${rootProject.mod.mc}"

base {
    archivesName.set(rootProject.mod.archivesBaseName)
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
    // withSourcesJar()
    // withJavadocJar()
}

repositories {
    mavenCentral()
    exclusiveContent {
        forRepository {
            maven("https://repo.spongepowered.org/repository/maven-public") { name = "Sponge" }
        }
        filter { includeGroupAndSubgroups("org.spongepowered") }
    }
    exclusiveContent {
        forRepositories(
            maven("https://maven.parchmentmc.org") { name = "ParchmentMC" },
            maven("https://maven.neoforged.net/releases") { name = "NeoForge" },
            maven("https://maven.minecraftforge.net/") { name = "MinecraftForge" }
        )
        filter { includeGroup("org.parchmentmc.data") }
    }
    exclusiveContent {
        forRepository {
            maven("https://api.modrinth.com/maven") { name = "Modrinth" }
        }
        filter { includeGroup("maven.modrinth") }
    }
    maven("https://maven.terraformersmc.com/releases/") { name = "TerraformersMC" }
}

tasks {
    processResources {
        val expandProps = mapOf(
            "version" to project.version as String,
            "mod_author" to rootProject.mod.propOrNull("mod_author"),
            "mod_id" to rootProject.mod.propOrNull("mod_id"),
            "mod_name" to rootProject.mod.propOrNull("mod_name"),
            "mod_source" to rootProject.mod.propOrNull("mod_source"),
            "mod_issues" to rootProject.mod.propOrNull("mod_issues"),
            "mod_license" to rootProject.mod.propOrNull("mod_license"),
            "mod_credits" to rootProject.mod.propOrNull("mod_credits"),
            "mod_curse" to rootProject.mod.propOrNull("mod_curse"),
            "mod_modrinth" to rootProject.mod.propOrNull("mod_modrinth"),
            "mod_donate" to rootProject.mod.propOrNull("mod_donate"),
            "mod_discord" to rootProject.mod.propOrNull("mod_discord"),
            "mod_description" to rootProject.mod.propOrNull("mod_description"),
            "fabric_mc_version_range" to rootProject.mod.propOrNull("fabric_mc_version_range"),
            "neoforge_mc_version_range" to rootProject.mod.propOrNull("neoforge_mc_version_range"),
            "fabric_api_version" to rootProject.mod.propOrNull("fabric_api_version"),
            "neoforge_version" to rootProject.mod.propOrNull("neoforge_version"),
            "neoforge_version_range" to rootProject.mod.propOrNull("neoforge_version_range")
        ).filterValues { it?.isNotEmpty() == true }.mapValues { (_, v) -> v!! }

        val jsonExpandProps = expandProps.mapValues { (_, v) -> v.replace("\n", "\\\\n") }

        filesMatching("META-INF/neoforge.mods.toml") {
            expand(expandProps)
        }

        filesMatching("fabric.mod.json") {
            expand(jsonExpandProps)
        }

        inputs.properties(expandProps)
    }
}
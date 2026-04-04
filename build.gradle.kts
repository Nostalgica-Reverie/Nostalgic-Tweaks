plugins {
    id("multiloader-common")
    id("org.relativitymc.neo-loom")
}

loom {
    accessWidenerPath = file("src/main/resources/nostalgic_tweaks.accesswidener")

    mixin {
        useLegacyMixinAp = false
    }

    decompilers {
        named("vineflower") { // Adds names to lambdas - useful for mixins
            options.put("mark-corresponding-synthetics", "1")
        }
    }

    runConfigs.all {
        ideConfigGenerated(false)
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${mod.mc}")

    compileOnly("net.fabricmc:fabric-loader:${mod.propOrNull("fabric_loader_version")}")
}

val commonJava: Configuration by configurations.creating {
    isCanBeResolved = false
    isCanBeConsumed = true
}

val commonResources: Configuration by configurations.creating {
    isCanBeResolved = false
    isCanBeConsumed = true
}

/*publishMods {
    val releaseType = rootProject.mod.getReleaseType()
    val modVersion: String = rootProject.mod.version

    changelog = rootProject.file("CHANGELOG.md").readText()
    type = ReleaseType.of(releaseType)

    github {
        accessToken = providers.environmentVariable("GITHUB_TOKEN")
        displayName = modVersion
        version = modVersion
        repository = "Nostalgica-Reverie/Nostalgic-Tweaks"
        tagName = providers.environmentVariable("GITHUB_REF_NAME")
        commitish = ""

        allowEmptyFiles = true
    }
}*/

artifacts {
    afterEvaluate {
        val mainSourceSet = sourceSets.main.get()
        mainSourceSet.java.sourceDirectories.files.forEach {
            add(commonJava.name, it)
        }
        mainSourceSet.resources.sourceDirectories.files.forEach {
            add(commonResources.name, it)
        }
    }
}
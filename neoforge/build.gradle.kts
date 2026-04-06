plugins {
    `multiloader-loader`
    id("org.relativitymc.neo-loom")
}

loom {
    accessWidenerPath = project(":").loom.accessWidenerPath

    runConfigs.all {
        ideConfigGenerated(true)
        runDir = "../run"
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${rootProject.mod.mc}")
    neoForge("net.neoforged:neoforge:${rootProject.mod.prop("neoforge_version")}")
}

loom.convertAw2At(tasks.jar, listOf("nostalgic_tweaks.classtweaker"))

/*
publishMods {
    file.set(tasks.jar.get().archiveFile)
}*/

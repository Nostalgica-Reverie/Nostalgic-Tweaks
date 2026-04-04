plugins {
    `multiloader-loader`
    id("org.relativitymc.neo-loom")
}

loom {
    runConfigs.all {
        ideConfigGenerated(true)
        runDir = "../run"
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${rootProject.mod.mc}")
    neoForge("net.neoforged:neoforge:${rootProject.mod.prop("neoforge_version")}")
}

//loom.convertAw2At(tasks.named("jar"), ["nostalgic_tweaks.accesswidener"])

/*
publishMods {
    file.set(tasks.jar.get().archiveFile)
}*/

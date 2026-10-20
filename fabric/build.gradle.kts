plugins {
    `multiloader-loader`
    id("org.relativitymc.neo-loom")
}

loom {
    accessWidenerPath = project(":").loom.accessWidenerPath

    runConfigs.all {
        generateRunConfig = true
        runDirectory.set(project.file("../run"))
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${rootProject.mod.mc}")

    implementation("net.fabricmc:fabric-loader:${rootProject.mod.prop("fabric_loader_version")}")
    api("net.fabricmc.fabric-api:fabric-api:${rootProject.mod.prop("fabric_api_version")}")

    compileOnly("com.terraformersmc:modmenu:${rootProject.mod.prop("fabric_modmenu_version")}")
}

/*
publishMods {
    file.set(tasks.jar.get().archiveFile)
    modrinth {
        requires("fabric-api")
    }

    curseforge {
        requires("fabric-api")
    }
}*/

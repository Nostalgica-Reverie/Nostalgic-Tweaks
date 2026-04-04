plugins {
    id("java")
    id("idea")
    id("multiloader-common")
//    id("me.modmuss50.mod-publish-plugin")
}

val commonJava: Configuration by configurations.creating {
    isCanBeResolved = true
}
val commonResources: Configuration by configurations.creating {
    isCanBeResolved = true
}

base {
    archivesName.set("${rootProject.mod.archivesBaseName}-$loader")
}

dependencies {
    compileOnly(project(path = ":"))
    commonJava(project(path = ":", configuration = "commonJava"))
    commonResources(project(path = ":", configuration = "commonResources"))
}

/*publishMods {
    val releaseType = rootProject.mod.getReleaseType()

    displayName = rootProject.mod.prop("mod_name") + " " + rootProject.mod.version
    version = project.version.toString() + "-" + loader

    changelog = rootProject.file("CHANGELOG.md").readText()
    type = ReleaseType.of(releaseType)

    modLoaders.addAll(supported_loaders!!)

    modrinth {
        accessToken = System.getenv("MODRINTH_TOKEN")
        projectId = rootProject.mod.prop("modrinth_project_id")
        minecraftVersions.addAll(rootProject.mod.prop("supported_versions").split(",").toList())
    }

    curseforge {
        accessToken = System.getenv("CURSEFORGE_TOKEN")
        projectId = rootProject.mod.prop("curseforge_project_id")
        minecraftVersions.addAll(rootProject.mod.prop("supported_versions").split(",").toList())
    }

    github {
        accessToken = System.getenv("GITHUB_TOKEN")
        parent(project(":").tasks.named("publishGithub"))
    }
}*/

tasks {
    compileJava {
        dependsOn(commonJava)
        source(commonJava)
    }

    processResources {
        dependsOn(commonResources)
        from(commonResources)
    }

    withType<Jar> {
        destinationDirectory = rootProject.layout.buildDirectory.dir("libs/$loader")
    }
}
plugins {
    `kotlin-dsl`
    kotlin("jvm") version "2.3.0"
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    fun plugin(id: String, version: String) = "$id:$id.gradle.plugin:$version"
    //TODO: maybe set this up later.
//    implementation(plugin("me.modmuss50.mod-publish-plugin", "1.1.0"))
}
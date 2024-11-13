import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    id("nyaadanbou-conventions.repositories")
    id("nyaadanbou-conventions.copy-jar")
    id("worldreset-conventions.commons")
    kotlin("plugin.serialization")
    id("org.jetbrains.kotlinx.atomicfu")
    alias(libs.plugins.pluginyml.paper)
}

group = "cc.mewcraft.worldreset"
version = "1.2.0-SNAPSHOT"
description = "Reset server worlds with cron expressions!"

dependencies {
    implementation(project(":bukkit-common"))
    implementation(local.cronscheduler)
    implementation(platform(libs.bom.cloud.paper))
    implementation(platform(libs.bom.cloud.kotlin)) {
        exclude("org.jetbrains.kotlin")
        exclude("org.jetbrains.kotlinx")
    }

    compileOnly(local.paper)
    compileOnly(local.helper)
    compileOnly(local.messenger)
    compileOnly(local.placeholderapi)
    compileOnly(local.miniplaceholders)
}

tasks {
    copyJar {
        environment = "paper"
        jarFileName = "worldreset-${project.version}.jar"
    }
}

paper {
    main = "cc.mewcraft.worldreset.WorldResetPlugin"
    name = "WorldReset"
    version = "${project.version}"
    description = project.description
    apiVersion = "1.19"
    author = "Nailm"
    serverDependencies {
        register("helper") {
            required = true
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
        register("Messenger") {
            required = true
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
        register("MiniPlaceholders") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.OMIT
        }
        register("PlaceholderAPI") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.OMIT
        }
    }
}

import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    id("nyaadanbou-conventions.repositories")
    id("nyaadanbou-conventions.copy-jar")
    id("worldreset-conventions.commons")
    alias(libs.plugins.pluginyml.paper)
}

group = "cc.mewcraft.worldreset"
version = "1.1.0-SNAPSHOT"
description = "Reset server worlds with cron expressions!"

dependencies {
    implementation(project(":bukkit-common"))
    implementation(local.cronscheduler)

    compileOnly(local.paper)
    compileOnly(local.helper)
    compileOnly(local.placeholderapi)
    compileOnly(local.miniplaceholders)
}

tasks {
    copyJar {
        environment = "paper"
        jarFileName = "worldreset-slave-${project.version}.jar"
    }
}

paper {
    main = "cc.mewcraft.worldreset.WorldResetPlugin"
    name = "WorldReset-Slave"
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

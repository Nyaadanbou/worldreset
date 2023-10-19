import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    id("cc.mewcraft.deploy-conventions")
    alias(libs.plugins.pluginyml.paper)
}

project.ext.set("name", "WorldReset")

group = "cc.mewcraft.worldreset"
version = "1.1.0"
description = "Reset server worlds with cron expressions!"

dependencies {
    // server
    compileOnly(libs.server.paper)

    // helper
    compileOnly(libs.helper)

    // plugin libs
    compileOnly(libs.papi)
    compileOnly(libs.minipapi)

    // internal
    implementation(project(":worldreset:common"))
    implementation(project(":spatula:bukkit:command"))
    implementation(project(":cronscheduler"))
    implementation(libs.cronutils)
    implementation(libs.bundles.mccoroutine.bukkit) {
        exclude("org.jetbrains.kotlin") // Excludes its Kotlin otherwise it breaks ours
    }
}

paper {
    main = "cc.mewcraft.worldreset.WorldResetPlugin"
    name = project.ext.get("name") as String
    version = "${project.version}"
    description = project.description
    apiVersion = "1.19"
    author = "Nailm"

    serverDependencies {
        register("Kotlin") {
            required = true
            joinClasspath = true
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
        register("helper") {
            required = true
            joinClasspath = true
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
        register("MiniPlaceholders") {
            required = false
            joinClasspath = true
            load = PaperPluginDescription.RelativeLoadOrder.OMIT
        }
        register("PlaceholderAPI") {
            required = false
            joinClasspath = true
            load = PaperPluginDescription.RelativeLoadOrder.OMIT
        }
    }
}

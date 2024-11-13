plugins {
    id("nyaadanbou-conventions.repositories")
    id("nyaadanbou-conventions.copy-jar")
    id("worldreset-conventions.commons")
}

group = "cc.mewcraft.worldreset"
version = "0.0.1-SNAPSHOT"
description = "Reset server worlds with cron expressions!"

dependencies {
    compileOnly(local.velocity); kapt(local.velocity)
    compileOnly(local.miniplaceholders)
}

tasks {
    copyJar {
        environment = "velocity"
        jarFileName = "worldreset-${project.version}.jar"
    }
}
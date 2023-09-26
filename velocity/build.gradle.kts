plugins {
    // id("cc.mewcraft.deploy-conventions") // TODO uncomment it when development starts
}

project.ext.set("name", "WorldReset-Velocity")

group = "cc.mewcraft.worldreset"
version = "1.0.0"
description = "Reset server worlds with cron expressions!"

dependencies {
    // server api
    compileOnly(libs.proxy.velocity)

    // libs that present as other plugins
    compileOnly(libs.minipapi)
}

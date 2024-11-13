plugins {
    id("nyaadanbou-conventions.repositories")
    id("worldreset-conventions.commons")
}

dependencies {
    compileOnly(local.cronscheduler)

    compileOnly(local.paper)
    compileOnly(local.helper)
    compileOnly(local.messenger)
    compileOnly(local.placeholderapi)
    compileOnly(local.miniplaceholders)
}

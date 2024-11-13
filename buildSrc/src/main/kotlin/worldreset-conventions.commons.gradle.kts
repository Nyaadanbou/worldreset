plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("com.gradleup.shadow")
}

// Expose version catalog
val local = the<org.gradle.accessors.dm.LibrariesForLocal>()

tasks {
    compileKotlin {
        compilerOptions {
            // we rely on IDE analysis
            suppressWarnings.set(true)
            freeCompilerArgs.add("-Xjvm-default=all")
        }
    }
    assemble {
        dependsOn(shadowJar)
    }
    shadowJar {
        archiveClassifier.set("shaded")
        dependencies {
            exclude("checkstyle.xml") // from cronutils
            exclude("META-INF/maven/**")
            exclude("META-INF/LICENSE*")
            exclude("META-INF/NOTICE*")
        }
    }
    test {
        useJUnitPlatform()
    }
}

java {
    withSourcesJar()
}

kotlin {
    jvmToolchain(21)

    sourceSets {
        val main by getting {
            dependencies {
                compileOnly(kotlin("stdlib"))
                compileOnly(kotlin("reflect"))
                compileOnly(local.kotlinx.coroutines.core)
                compileOnly(local.kotlinx.serialization.json)
            }
        }
        val test by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("stdlib"))
                implementation(kotlin("reflect"))
                implementation(local.kotlinx.coroutines.core)
                implementation(local.kotlinx.serialization.json)
            }
        }
    }
}
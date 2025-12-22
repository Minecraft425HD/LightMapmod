rootProject.name = "voxelmap"

pluginManagement {
    repositories {
        mavenLocal()
        maven { url = uri("https://maven.minecraftforge.net/") }
        gradlePluginPortal()
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "net.minecraftforge.gradle") {
                useModule("net.minecraftforge.gradle:ForgeGradle:6.0.+")
            }
        }
    }
}

include("common")
include("forge")
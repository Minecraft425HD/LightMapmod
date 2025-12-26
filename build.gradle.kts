plugins {
    id("java")
}

val MINECRAFT_VERSION by extra { "1.20.1" }
val FORGE_VERSION by extra { "47.4.0" }
val LIGHTMAP_VERSION by extra { "1.0.0" }

val MOD_VERSION by extra { "$MINECRAFT_VERSION-$LIGHTMAP_VERSION" }

allprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.jar {
    enabled = false
}

subprojects {
    apply(plugin = "maven-publish")

    java.toolchain.languageVersion = JavaLanguageVersion.of(17)

    tasks.processResources {
        filesMatching("META-INF/mods.toml") {
            expand(mapOf("version" to MOD_VERSION))
        }
    }

    version = MOD_VERSION
    group = "com.lightmap"

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(17)
    }

    tasks.withType<GenerateModuleMetadata>().configureEach {
        enabled = false
    }
}

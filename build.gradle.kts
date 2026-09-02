plugins {
    java
}

group = "io.github.naysurgithub"
version = providers.gradleProperty("version").get()

repositories {
    mavenCentral()
    maven {
        name = "powerNukkitXReleases"
        url = uri("https://repo.powernukkitx.org/releases")
    }
    maven("https://repo.opencollab.dev/maven-releases/")
    maven("https://repo.opencollab.dev/maven-snapshots/") {
        mavenContent {
            snapshotsOnly()
        }
    }
}

dependencies {
    compileOnly("org.powernukkitx:server:${providers.gradleProperty("pnxVersion").get()}")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks.processResources {
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand("version" to project.version)
    }
}

tasks.jar {
    archiveBaseName.set("GraveStone")
}

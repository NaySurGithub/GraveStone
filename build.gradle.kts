plugins {
    java
}

group = providers.gradleProperty("group").get()
version = providers.gradleProperty("version").get()

repositories {
    mavenCentral()
    maven("https://repo.powernukkitx.org/releases")
    maven("https://repo.powernukkitx.org/snapshots")
    maven("https://repo.opencollab.dev/maven-releases/")
    maven("https://repo.opencollab.dev/maven-snapshots/")
}

dependencies {
    // Drop a local PowerNukkitX jar into libs/ to build against it instead of the remote artifact.
    val localPnx = file("libs")
        .listFiles { f -> f.extension == "jar" }
        ?.sortedBy { it.name }
        ?.firstOrNull()
    if (localPnx != null) {
        compileOnly(files(localPnx))
    } else {
        compileOnly("org.powernukkitx:server:${providers.gradleProperty("pnxVersion").get()}")
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.processResources {
    filesMatching("plugin.yml") {
        expand("version" to project.version)
    }
}

tasks.jar {
    archiveBaseName.set("GraveStone")
}

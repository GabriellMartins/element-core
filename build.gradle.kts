import java.net.URL

plugins {
    java
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("net.minecrell.plugin-yml.bukkit") version "0.3.0"
    id("com.palantir.git-version") version "3.0.0"
}

group = "dev.github.gabrielmartins"

val gitVersion: groovy.lang.Closure<*> by extra
version = "1.0.0-${gitVersion()}"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.20.4-R0.1-SNAPSHOT")
    implementation("org.reflections:reflections:0.10.2")

    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

bukkit {
    name = "Element-core"
    version = "1.0.0"
    main = "dev.github.gabrielmartins.Engine"
    authors = listOf("Martins")
    description = "Test plugin for Rollerite (based on [commands, permissions, configurable messages])"
    apiVersion = "1.20"
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks.compileJava {
    options.encoding = "UTF-8"
    options.release.set(17)
}

tasks.shadowJar {
    archiveClassifier.set("")
    minimize()
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

val paperVersion = "1.21.4"
val paperBuild = "56"
val paperJarName = "paper-$paperVersion-$paperBuild.jar"
val paperJarFile = file("run-server/$paperJarName")
val paperDownloadUrl = "https://api.papermc.io/v2/projects/paper/versions/$paperVersion/builds/$paperBuild/downloads/$paperJarName"

val downloadPaper by tasks.registering {
    outputs.file(paperJarFile)
    doLast {
        if (!paperJarFile.exists()) {
            println("Downloading Paper $paperVersion build $paperBuild...")
            paperJarFile.parentFile.mkdirs()
            URL(paperDownloadUrl).openStream().use { input ->
                paperJarFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            println("Paper downloaded at: ${paperJarFile.absolutePath}")
        }
    }
}

val runServer by tasks.registering(Exec::class) {
    group = "minecraft"
    description = "Runs a local test Paper server with the plugin"

    val serverDir = layout.projectDirectory.dir("run-server")
    val pluginsDir = serverDir.dir("plugins")
    val opsFile = serverDir.file("ops.json").asFile

    dependsOn(downloadPaper)
    dependsOn(tasks.shadowJar)

    doFirst {
        // Create plugin and server folders
        pluginsDir.asFile.mkdirs()

        // Accept EULA
        file("run-server/eula.txt").writeText("eula=true\n")

        // Copy plugin jar
        copy {
            from(tasks.shadowJar.get().archiveFile)
            into(pluginsDir)
        }

        val opsJson = """
            [
              {
                "uuid": "05decf7b-27eb-4551-a31e-49e1fd132a5a",
                "name": "rapaziada",
                "level": 4,
                "bypassesPlayerLimit": true
              }
            ]
        """.trimIndent()

        opsFile.writeText(opsJson)
    }

    workingDir = serverDir.asFile
    commandLine("java", "-jar", paperJarName, "nogui")
}

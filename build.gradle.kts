import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val spaceSdkVersion: String by project
val kotlinVersion: String by project
val ktorVersion: String by project
val logbackVersion: String by project
val exposedVersion: String by project
val hikariVersion: String by project
val postgresDriverVersion: String by project
val kotlinxSerializationVersion: String by project

plugins {
    kotlin("jvm") version "1.7.0"
    kotlin("plugin.serialization") version "1.7.0"
    id("com.avast.gradle.docker-compose") version "0.14.0"
    id("io.ktor.plugin") version "2.1.3"
    application
}

group = "com.niphoneua.wikipediapreviews"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.jetty.EngineMain")
}

jib {
    container.mainClass = "io.ktor.server.jetty.EngineMain"
}

repositories {
    maven("https://maven.pkg.jetbrains.space/public/p/space/maven")

    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-jetty-jvm:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")

    implementation("org.jetbrains:space-sdk-jvm:$spaceSdkVersion")

    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
    implementation("com.zaxxer:HikariCP:$hikariVersion")
    implementation("org.postgresql:postgresql:$postgresDriverVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion")

    testImplementation(kotlin("test"))
}

kotlin.sourceSets.all {
    languageSettings {
        optIn("kotlin.time.ExperimentalTime")
        optIn("io.ktor.server.locations.KtorExperimentalLocationsAPI")
        optIn("space.jetbrains.api.ExperimentalSpaceSdkApi")
    }
}

dockerCompose {
    projectName = "wikipedia-previews-db"
    removeContainers = false
    removeVolumes = false
}

tasks {
    val run by getting(JavaExec::class)
    dockerCompose.isRequiredBy(run)
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs += "-opt-in=io.ktor.util.KtorExperimentalAPI"
}

ktor {
    fatJar {
        archiveFileName.set("fat.jar")
    }

    docker {
        localImageName.set("wikipedia-links-preview")
        imageTag.set("0.0.4")
        portMappings.set(listOf(
            io.ktor.plugin.features.DockerPortMapping(
                80,
                80,
                io.ktor.plugin.features.DockerPortMappingProtocol.TCP
            )
        ))
    }
}

plugins {
    kotlin("jvm") version "2.3.0"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.0"
}

group = "com.github.snaffys"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.exposed:exposed-core:0.50.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.50.1")

    implementation("org.xerial:sqlite-jdbc:3.45.1.0")

    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(25)
}

tasks.test {
    useJUnitPlatform()
}

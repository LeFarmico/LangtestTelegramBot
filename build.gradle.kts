import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.10"
    application
}

group = "me.flyin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.telegram:telegrambotsextensions:5.7.1")
    implementation("com.google.code.gson:gson:2.8.9")
    annotationProcessor("org.apache.logging.log4j:log4j-core:2.17.1")
    implementation("org.apache.logging.log4j:log4j-api-kotlin:1.1.0")
    implementation("org.slf4j:slf4j-simple:1.7.35")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}

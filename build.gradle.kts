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
    implementation("io.insert-koin:koin-core:3.1.6")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.1")

    // Telegram bot api
    implementation("org.telegram:telegrambotsextensions:6.0.1")

    // Gson
    implementation("com.google.code.gson:gson:2.9.0")

    // Logging
    annotationProcessor("org.apache.logging.log4j:log4j-core:2.17.1")
    implementation("org.apache.logging.log4j:log4j-api-kotlin:1.1.0")
    implementation("org.slf4j:slf4j-simple:1.7.35")

    // Deps
    implementation(project(":domain"))
    implementation(project(":data"))
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

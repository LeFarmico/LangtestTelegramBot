plugins {
    kotlin("jvm")
}

group = "me.flyin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":domain"))
}
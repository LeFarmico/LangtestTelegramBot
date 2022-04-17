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

//    OkHttpClient
    implementation("com.squareup.okhttp:okhttp:2.7.5")
}

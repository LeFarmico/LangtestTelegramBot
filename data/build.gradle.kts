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

//      OkHttpClient
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")

//      Koin
    implementation("io.insert-koin:koin-core:3.1.6")

//      Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.retrofit2:adapter-rxjava2:2.9.0")

//      GSON
    implementation("com.google.code.gson:gson:2.9.0")
}

plugins {
    // Apply the Application plugin to add support for building an executable JVM application.
    application
    kotlin("jvm") version libs.versions.kotlin
    kotlin("plugin.serialization") version libs.versions.kotlin
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

dependencies {
    // Ktor Server
    implementation(libs.ktorCoreServer)
    implementation(libs.nettyJvm)
    implementation(libs.ktorServerContentNegotiation)
    implementation(libs.ktorSerialization)

    implementation(libs.slf4jNop)

    // Ktor Client
    testImplementation(libs.ktorCore)
    testImplementation(libs.ktorClient)
    testImplementation(libs.ktorClientEngine)

    // Kotlin Tests
    testImplementation(libs.kotlinTestJunit)
    testImplementation(libs.kotlinJupiterApi)
    testImplementation(libs.kotlinJupiterEngine)
}

application {
    // Define the Fully Qualified Name for the application main class
    // (Note that Kotlin compiles `App.kt` to a class with FQN `com.example.app.AppKt`.)
//    mainClass = "br.com.pedrocorcaque.app.AppKt"
    mainClass = "io.ktor.server.netty.EngineMain"
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
    }
}

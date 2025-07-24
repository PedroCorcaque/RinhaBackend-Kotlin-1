plugins {
    // Apply the Application plugin to add support for building an executable JVM application.
    application
    kotlin("jvm") version libs.versions.kotlin
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

dependencies {
    implementation(libs.ktorCore)
    implementation(libs.ktorClient)
    implementation(libs.ktorClientEngine)
    implementation(libs.ktorSerialization)

    implementation(libs.ktorCoreServer)

    implementation(libs.slf4jNop)

    testImplementation(libs.kotlinTestJunit)
    testImplementation(libs.kotlinJupiterApi)
    testImplementation(libs.kotlinJupiterEngine)
}

application {
    // Define the Fully Qualified Name for the application main class
    // (Note that Kotlin compiles `App.kt` to a class with FQN `com.example.app.AppKt`.)
    mainClass = "br.com.pedrocorcaque.app.AppKt"
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
    }
}

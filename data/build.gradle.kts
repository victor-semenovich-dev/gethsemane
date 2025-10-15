plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.room)
}
java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
    }
}
room {
    schemaDirectory("$projectDir/roomSchemas")
}
dependencies {
    api(files("libs/activeandroid-3.1.0-SNAPSHOT.jar"))
    implementation(project(":domain"))
    api(libs.bundles.ktor)
}
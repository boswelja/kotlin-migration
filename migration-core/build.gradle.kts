import Publishing.configureMavenPublication

plugins {
    kotlin("multiplatform")
    id("maven-publish")
    id("signing")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            implementation(libs.kotlinx.coroutines.core)
        }
        val commonTest by getting {
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.strikt.core)
            implementation(libs.mockk.core)
        }
    }
}

group = Publishing.groupId
version = Publishing.version ?: "0.1.0"

// Build sources too
java {
    withSourcesJar()
    withJavadocJar()
}

// Add name and version to manifest
tasks.jar {
    manifest {
        attributes(
            mapOf(
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version
            )
        )
    }
}

publishing {
    publications {
        create(
            "release",
            configureMavenPublication(
                project.name,
                "A Kotlin library to enable easier program migrations, inspired by AndroidX Room",
                "https://github.com/boswelja/android-migration",
            ) {
                from(components["java"])
            }
        )
    }
    repositories(Publishing.repositories)
}

// Create signing config
ext["signing.keyId"] = Publishing.signingKeyId
ext["signing.password"] = Publishing.signingPassword
ext["signing.secretKeyRingFile"] = Publishing.signingSecretKeyring
signing {
    sign(publishing.publications)
}

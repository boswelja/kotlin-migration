plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.dokka") version "1.6.0"
    id("io.gitlab.arturbosch.detekt") version "1.19.0"
    `maven-publish`
    signing
}

kotlin {
    jvm()
    android {
        publishLibraryVariants("release")
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libs.kotlinx.coroutines.core)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit5"))
            }
        }
        val androidMain by getting {
            dependencies { }
        }
        val androidTest by getting {
            dependencies {
                implementation(kotlin("test-junit5"))
            }
        }
    }
}

android {
    compileSdk = 31
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 21
        targetSdk = 31
    }
}

group = Publishing.groupId
version = Publishing.version ?: "0.1.0"
description = "A Kotlin library to enable easier code migrations, inspired by AndroidX Room"

ext["signing.keyId"] = Publishing.signingKeyId
ext["signing.password"] = Publishing.signingPassword
ext["signing.secretKeyRingFile"] = Publishing.signingSecretKeyring

tasks {
    create<Jar>("javadocJar") {
        dependsOn(dokkaJavadoc)
        archiveClassifier.set("javadoc")
        from(dokkaJavadoc.get().outputDirectory)
    }
}

signing {
    sign(publishing.publications)
}

afterEvaluate {
    publishing {
        publications.withType<MavenPublication> {
            artifact(tasks["javadocJar"])

            pom {
                name.set(this@afterEvaluate.name)
                description.set(this@afterEvaluate.description)
                url.set("https://github.com/boswelja/kotlin-migration")
                licenses {
                    license {
                        name.set("Apache 2.0")
                        url.set("https://github.com/boswelja/kotlin-migration/blob/main/LICENSE")
                    }
                }
                developers {
                    developer {
                        id.set("boswelja")
                        name.set("Jack Boswell")
                        email.set("boswelja@outlook.com")
                        url.set("https://github.com/boswelja")
                    }
                }
                scm {
                    connection.set("scm:git:github.com/boswelja/kotlin-migration.git")
                    developerConnection.set("scm:git:ssh://github.com/boswelja/kotlin-migration.git")
                    url.set("https://github.com/boswelja/kotlin-migration")
                }
            }
            repositories(Publishing.repositories)
        }
    }
}

detekt {
    config = files("$rootDir/config/detekt/detekt.yml")
    source = files("src")
    buildUponDefaultConfig = true
    parallel = true
}

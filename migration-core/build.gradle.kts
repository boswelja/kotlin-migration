plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("maven-publish")
    id("signing")
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
        val androidMain by getting
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

ext["signing.keyId"] = Publishing.signingKeyId
ext["signing.password"] = Publishing.signingPassword
ext["signing.secretKeyRingFile"] = Publishing.signingSecretKeyring
signing {
    sign(publishing.publications)
}
afterEvaluate {
    publishing {
        publications.withType<MavenPublication> {
            groupId = Publishing.groupId
            artifactId = project.name
            version = Publishing.version

            pom {
                name.set(artifactId)
                this.description.set(description)
                this.url.set(url)

                licenses {
                    license {
                        name.set("Apache 2.0")
                        url.set("https://github.com/boswelja/android-migration/blob/main/LICENSE")
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

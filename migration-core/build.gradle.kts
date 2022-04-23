plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.dokka") version "1.6.20"
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

tasks {
    create<Jar>("javadocJar") {
        dependsOn(dokkaJavadoc)
        archiveClassifier.set("javadoc")
        from(dokkaJavadoc.get().outputDirectory)
    }
}

signing {
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications)
}

publishing {
    repositories {
        maven("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/") {
            val ossrhUsername: String? by project
            val ossrhPassword: String? by project
            name = "sonatype"
            credentials {
                username = ossrhUsername
                password = ossrhPassword
            }
        }
    }
    publications.withType<MavenPublication> {
        artifact(tasks["javadocJar"])

        pom {
            name.set("migration-core")
            description.set("A Kotlin library to enable easier program migrations, inspired by AndroidX Room")
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
    }
}

detekt {
    config = files("$rootDir/config/detekt/detekt.yml")
    source = files("src")
    buildUponDefaultConfig = true
    parallel = true
}

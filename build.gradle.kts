buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.21")
        classpath("com.android.tools.build:gradle:7.3.0-alpha09")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

plugins {
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
}

group = "io.github.boswelja.migration"
version = findProperty("version") ?: "0.1.0"

nexusPublishing {
    repositories {
        sonatype {
            val ossrhUsername: String? by project
            val ossrhPassword: String? by project
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl
                .set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
            username.set(ossrhUsername)
            password.set(ossrhPassword)
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

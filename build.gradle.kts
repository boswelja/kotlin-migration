plugins {
    id("com.android.library") version "7.3.0-alpha09" apply false
    id("org.jetbrains.kotlin.multiplatform") version "1.7.10" apply false
    id("org.jetbrains.kotlinx.kover") version "0.5.1" apply false
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
    id("io.gitlab.arturbosch.detekt") version "1.21.0"
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

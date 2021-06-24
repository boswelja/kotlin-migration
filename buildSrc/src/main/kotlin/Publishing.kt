import java.net.URI
import org.gradle.api.Action
import org.gradle.api.artifacts.DependencySet
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.publish.maven.MavenPomDeveloperSpec
import org.gradle.api.publish.maven.MavenPomLicenseSpec
import org.gradle.api.publish.maven.MavenPomScm
import org.gradle.api.publish.maven.MavenPublication

object Publishing {

    val version: String?
        get() = System.getenv("VERSION")

    val ossrhUsername: String?
        get() = System.getenv("OSSRH_USERNAME")
    val ossrhPassword: String?
        get() = System.getenv("OSSRH_PASSWORD")

    const val groupId = "io.github.boswelja.watchconnection"

    val signingKeyId: String?
        get() = System.getenv("SIGNING_KEY_ID")
    val signingPassword: String?
        get() = System.getenv("SIGNING_PASSWORD")
    val signingSecretKeyring: String?
        get() = System.getenv("SIGNING_SECRET_KEY_RING_FILE")

    val scm: Action<MavenPomScm> = Action {
        connection.set("scm:git:github.com/boswelja/android-migration.git")
        developerConnection.set("scm:git:ssh://github.com/boswelja/android-migration.git")
        url.set("https://github.com/boswelja/android-migration")
    }

    val licenses: Action<MavenPomLicenseSpec> = Action {
        license {
            name.set("Apache 2.0")
            url.set("https://github.com/boswelja/android-migration/blob/main/LICENSE")
        }
    }

    val developers: Action<MavenPomDeveloperSpec> = Action {
        developer {
            id.set("boswelja")
            name.set("Jack Boswell")
            email.set("boswelja@outlook.com")
            url.set("https://boswelja.github.io")
        }
    }

    val repositories: Action<RepositoryHandler> = Action {
        maven {
            name = "sonatype"
            url = URI("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = ossrhUsername
                password = ossrhPassword
            }
        }
    }

    fun configureMavenPublication(
        artifactId: String,
        description: String,
        url: String,
        dependencySet: DependencySet,
        configuration: MavenPublication.() -> Unit
    ): MavenPublication.() -> Unit = {
        configuration()
        this.groupId = this@Publishing.groupId
        this.artifactId = artifactId
        version = this@Publishing.version

        pom {
            name.set(artifactId)
            this.description.set(description)
            this.url.set(url)

            licenses(licenses)
            developers(developers)
            scm(scm)

            withXml {
                val dependenciesNode = asNode().appendNode("dependencies")

                dependencySet.forEach {
                    // Replace implementation(project()) with something more meaningful
                    if (it.group == "android-migration") {
                        val dependencyNode = dependenciesNode.appendNode("dependency")
                        dependencyNode.appendNode("groupId", groupId)
                        dependencyNode.appendNode("artifactId", it.name)
                        dependencyNode.appendNode("version", version)
                    } else if (it.group != null && it.version != null) {
                        val dependencyNode = dependenciesNode.appendNode("dependency")
                        dependencyNode.appendNode("groupId", it.group)
                        dependencyNode.appendNode("artifactId", it.name)
                        dependencyNode.appendNode("version", it.version)
                    }
                }
            }
        }
    }
}

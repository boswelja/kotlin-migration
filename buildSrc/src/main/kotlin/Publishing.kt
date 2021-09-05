import org.gradle.api.Action
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.kotlin.dsl.maven

object Publishing {

    val version: String?
        get() = System.getenv("VERSION")

    val ossrhUsername: String?
        get() = System.getenv("OSSRH_USERNAME")
    val ossrhPassword: String?
        get() = System.getenv("OSSRH_PASSWORD")

    const val groupId = "io.github.boswelja.migration"

    val signingKeyId: String?
        get() = System.getenv("SIGNING_KEY_ID")
    val signingPassword: String?
        get() = System.getenv("SIGNING_PASSWORD")
    val signingSecretKeyring: String?
        get() = System.getenv("SIGNING_SECRET_KEY_RING_FILE")

    val repositories: Action<RepositoryHandler> = Action {
        maven("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/") {
            name = "sonatype"
            credentials {
                username = ossrhUsername
                password = ossrhPassword
            }
        }
    }
}

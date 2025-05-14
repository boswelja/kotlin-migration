import org.jetbrains.kotlin.allopen.gradle.AllOpenExtension

plugins {
    id("kotlin")
    id("org.jetbrains.kotlin.plugin.allopen") version "2.1.21"
    id("org.jetbrains.kotlinx.benchmark") version "0.4.14"
}

configure<AllOpenExtension> {
    annotation("org.openjdk.jmh.annotations.State")
}

dependencies {
    implementation(projects.migrationCore)

    implementation(libs.kotlinx.benchmark)
}

benchmark {
    configurations {
        named("main")
    }
    targets {
        register("main") {
            this as kotlinx.benchmark.gradle.JvmBenchmarkTarget
            jmhVersion = "1.21"
        }
    }
}

plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm()
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libs.kotlinx.coroutines.core)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.strikt.core)
                implementation(libs.mockk.core)
            }
        }
    }
}

group = Publishing.groupId
version = Publishing.version ?: "0.1.0"

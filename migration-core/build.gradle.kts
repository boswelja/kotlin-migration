plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm()
    js()

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

description = "Ktor client JSON support"

plugins {
    id("kotlinx-serialization")
}

kotlin {
    sourceSets {
        // This is a workaround for https://youtrack.jetbrains.com/issue/KT-39037
        fun excludingSelf(dependency: Any) = project.dependencies.create(dependency).apply {
            (this as ModuleDependency).exclude(module = project.name)
        }

        commonMain {
            dependencies {
                api(project(":ktor-client:ktor-client-core"))
            }
        }
        commonTest {
            dependencies {
                api(excludingSelf(project(":ktor-client:ktor-client-tests")))
                api(excludingSelf(project(":ktor-client:ktor-client-features:ktor-client-json:ktor-client-serialization")))
            }
        }
        jvmTest {
            dependencies {
                api(excludingSelf(project(":ktor-client:ktor-client-tests")))
                api(project(":ktor-client:ktor-client-features:ktor-client-json:ktor-client-gson"))

                runtimeOnly(project(":ktor-client:ktor-client-apache"))
                runtimeOnly(project(":ktor-client:ktor-client-cio"))
                runtimeOnly(project(":ktor-client:ktor-client-android"))
                runtimeOnly(project(":ktor-client:ktor-client-okhttp"))
//                runtimeOnly(project(":ktor-client:ktor-client-jetty"))
            }
        }
        jsTest {
            dependencies {
                api(project(":ktor-client:ktor-client-js"))
            }
        }

//        if (!isIdeaActive) {
//            configure(listOf(getByName("macosX64Test"), getByName("iosX64Test"))) {
//                dependencies {
//                    implementation(project(":ktor-client:ktor-client-ios"))
//                }
//            }
//            configure(listOf(getByName("linuxX64Test"), getByName("macosX64Test"), getByName("mingwX64Test"))) {
//                dependencies {
//                    implementation(project(":ktor-client:ktor-client-curl"))
//                }
//            }
//        }
    }
}

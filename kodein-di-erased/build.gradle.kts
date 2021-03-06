plugins {
    id("org.kodein.library.mpp")
}

kodein {
    kotlin {

        common {
            main.dependencies {
                api(project(":kodein-di-core"))
            }

            test.dependencies {
                implementation(project(":test-utils"))
            }
        }

        add(kodeinTargets.jvm) {
            target.setCompileClasspath()

            test.dependencies {
                implementation("org.jetbrains.kotlin:kotlin-reflect")
            }
        }

        add(kodeinTargets.js)

        add(kodeinTargets.native.all)

    }
}

kodeinUpload {
    name = "Kodein-DI-Erased"
    description = "KODEIN Dependency Injection using erased types by default"
}

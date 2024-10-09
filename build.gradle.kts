import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") apply false
    id("org.jetbrains.dokka") version "1.9.20" apply false
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0"
}


subprojects {

    plugins.withType<JavaPlugin> {
        configure<JavaPluginExtension> {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
        }
    }

    plugins.withId("org.jetbrains.kotlin.jvm") {
        tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
            compilerOptions {
                jvmTarget.set(JvmTarget.JVM_11)
            }
        }

        dependencies {
            "implementation"(kotlin("stdlib-jdk8"))
        }
    }

    plugins.withType<MavenPublishPlugin> {
        apply(from = "$rootDir/gradle/publishing.gradle.kts")
    }

    plugins.withId("org.jetbrains.dokka") {

        dependencies {
            "dokkaJavadocPlugin"("org.jetbrains.dokka:kotlin-as-java-plugin:1.9.20")
        }

        tasks.withType<Jar>().matching { it.name == "javadocJar" }
            .configureEach {
                from(tasks.named("dokkaJavadoc"))
            }

        tasks.withType<org.jetbrains.dokka.gradle.DokkaTask> {
            dokkaSourceSets {
                named("main") {
                    sourceLink {
                        val githubUrl = project.extra["github.url"] as String
                        localDirectory.set(project.file("src/main/kotlin"))
                        remoteUrl.set(java.net.URL("$githubUrl/tree/master/"))
                        remoteLineSuffix.set("#L")
                    }
                }
            }
        }
    }
}


nexusPublishing {
    repositories {
        sonatype()
    }
}

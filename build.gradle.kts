import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.dokka)
    alias(libs.plugins.sonatype.publish)
    id("maven-publish")
    id("signing")
}

fun findProperty(name: String): String? = if (hasProperty(name)) property(name) as String else System.getenv(name)
fun findFilledProperty(name: String): String? = findProperty(name)?.ifBlank { null }
fun booleanProperty(name: String): Boolean = findProperty(name)?.toBoolean() ?: false

group = findProperty("group")!!
version = findProperty("version")!!

val ossrhUsername = findFilledProperty("ossrh.username")
val ossrhPassword = findFilledProperty("ossrh.password")
val ossrhMavenEnabled = ossrhUsername != null && ossrhPassword != null
val isSigningEnabled = findFilledProperty("signing.keyId") != null &&
        findFilledProperty("signing.password") != null &&
        findFilledProperty("signing.secretKeyRingFile") != null

val jvmOnly = booleanProperty("debug.jvmOnly")
val isAppleDevice = org.gradle.internal.os.OperatingSystem.current().isMacOsX

kotlin {
    jvm {
        withSourcesJar(true)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
        testRuns.named("test") {
            executionTask.configure { useJUnitPlatform() }
        }
    }

    // web

    if (!jvmOnly) {
        js {
            browser()
        }

        @OptIn(ExperimentalWasmDsl::class)
        wasmJs { d8() }
        @OptIn(ExperimentalWasmDsl::class)
        wasmWasi { nodejs() }

        // https://kotlinlang.org/docs/native-target-support.html

        if (isAppleDevice) {
            // Tier1
            macosArm64() // iOS only
            iosSimulatorArm64() // iOS only
            iosArm64() // iOS only

            // Tier2
            watchosSimulatorArm64() // iOS only
            watchosArm64() // iOS only
            tvosSimulatorArm64() // iOS only
            tvosArm64() // iOS only

            // Tier3
            watchosDeviceArm64() // iOS only
            iosX64() // iOS only
        }

        // Tier2
        linuxX64()
        linuxArm64()

        // Tier3
        androidNativeArm32()
        androidNativeArm64()
        androidNativeX86()
        androidNativeX64()
        mingwX64()
    }

    compilerOptions {
        freeCompilerArgs.add("-XXLanguage:+FullValueClasses")
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.serialization.json)
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

tasks.withType(JavaCompile::class.java) {
    sourceCompatibility = JavaVersion.VERSION_17.toString()
    targetCompatibility = JavaVersion.VERSION_17.toString()
}

val kotlinVersion = libs.versions.kotlin.asProvider().get().substringBeforeLast(".").let { KotlinVersion.fromVersion(it) }

tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask::class.java) {
    compilerOptions {
        apiVersion.set(kotlinVersion)
        languageVersion.set(kotlinVersion)
    }
}

publishing {
    publications.withType<MavenPublication> {
        val publicationName = this@withType.name
        val javadocJar = tasks.register("${publicationName}JavadocJar", Jar::class) {
            archiveClassifier.set("javadoc")
            archiveBaseName.set("${archiveBaseName.get()}-${publicationName}")
        }
        artifact(javadocJar)
        pom {
            name.set(findProperty("POM_NAME")!!)
            description.set(findProperty("POM_DESCRIPTION")!!)
            url.set(findProperty("POM_URL")!!)
            licenses {
                license {
                    name.set(findProperty("POM_LICENSE_NAME")!!)
                    url.set(findProperty("POM_LICENSE_URL")!!)
                }
            }
            developers {
                developer {
                    id.set(findProperty("POM_DEVELOPER_LBRIAND_ID")!!)
                    name.set(findProperty("POM_DEVELOPER_LBRIAND_NAME")!!)
                    email.set(findProperty("POM_DEVELOPER_LBRIAND_EMAIL")!!)
                }
            }
            scm {
                connection.set(findProperty("POM_SCM_URL")!!)
                developerConnection.set(findProperty("POM_SCM_CONNECTION")!!)
                url.set(findProperty("POM_SCM_DEV_CONNECTION")!!)
            }
        }
    }

    repositories {
        mavenLocal()
    }
}

if (ossrhMavenEnabled) {
    nexusPublishing {
        repositories {
            sonatype {
                username = ossrhUsername
                password = ossrhPassword
                nexusUrl.set(uri("https://ossrh-staging-api.central.sonatype.com/service/local/"))
                snapshotRepositoryUrl.set(uri("https://central.sonatype.com/repository/maven-snapshots/"))
            }
        }
    }
}

if (isSigningEnabled) {
    signing {
        sign(publishing.publications)
    }
}

tasks.register<Delete>("cleanupGithubDocumentation") {
    description = "Clean up the generated Github documentation"
    delete(file("docs"))
}

tasks.register<Copy>("generateGithubDocumentation") {
    description = "Generate the Github documentation"
    dependsOn("cleanupGithubDocumentation")
    dependsOn("dokkaGeneratePublicationHtml")
    val buildDir = layout.buildDirectory
    from(buildDir.dir("dokka/html")).into("docs")
}
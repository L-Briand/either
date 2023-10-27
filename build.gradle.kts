plugins {
    kotlin("multiplatform") version "1.9.0"
    kotlin("plugin.serialization") version "1.9.0"
    id("maven-publish")
    id("signing")
}

fun findProperty(name: String): String? = if (hasProperty(name)) property(name) as String else System.getenv(name)
fun findFilledProperty(name: String): String? = findProperty(name)?.ifBlank { null }

group = findProperty("group") !!
version = findProperty("version") !!

val ossrhUsername = findFilledProperty("osshr.username")
val ossrhPassword = findFilledProperty("osshr.password")
val ossrhMavenEnabled = ossrhUsername != null && ossrhPassword != null
val isSigningEnabled = findFilledProperty("signing.keyId") != null &&
        findFilledProperty("signing.password") != null &&
        findFilledProperty("signing.secretKeyRingFile") != null

repositories {
    mavenCentral()
}

kotlin {
    jvm {
        jvmToolchain(8)
        withJava()
        testRuns.named("test") {
            executionTask.configure { useJUnitPlatform() }
        }
    }

    js("js") {
        browser()
        nodejs()
    }

    macosArm64("macosArm64")
    macosX64("macosX64")
    linuxArm64("linuxArm64")
    linuxX64("linuxX64")
    mingwX64("mingwX64")

    sourceSets {
        getByName("commonMain") {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
            }
        }
        getByName("commonTest") {
            dependencies {
                implementation(kotlin("test"))
            }
        }
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
            name.set(findProperty("POM_NAME") !!)
            description.set(findProperty("POM_DESCRIPTION") !!)
            url.set(findProperty("POM_URL") !!)
            licenses {
                license {
                    name.set(findProperty("POM_LICENSE_NAME") !!)
                    url.set(findProperty("POM_LICENSE_URL") !!)
                }
            }
            developers {
                developer {
                    id.set(findProperty("POM_DEVELOPER_LBRIAND_ID") !!)
                    name.set(findProperty("POM_DEVELOPER_LBRIAND_NAME") !!)
                    email.set(findProperty("POM_DEVELOPER_LBRIAND_EMAIL") !!)
                }
            }
            scm {
                connection.set(findProperty("POM_SCM_URL") !!)
                developerConnection.set(findProperty("POM_SCM_CONNECTION") !!)
                url.set(findProperty("POM_SCM_DEV_CONNECTION") !!)
            }
        }
    }

    repositories {
        mavenLocal()
        if (ossrhMavenEnabled) {
            maven {
                name = "sonatype"
                setUrl("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
                credentials {
                    username = ossrhUsername
                    password = ossrhPassword
                }
            }
        }
    }
}

if (isSigningEnabled) {
    signing {
        sign(publishing.publications)
    }
}

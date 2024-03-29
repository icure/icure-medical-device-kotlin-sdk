import com.github.jk1.license.render.CsvReportRenderer
import com.github.jk1.license.render.ReportRenderer

val kotlinVersion = "1.4.32"
val kotlinCoroutinesVersion = "1.4.3"
val jacksonVersion = "2.12.5"

plugins {
    kotlin("jvm") version "1.4.32"
    kotlin("kapt") version "1.4.32"
    id("jacoco")
    id("org.sonarqube") version "3.3"
    id("com.github.jk1.dependency-license-report") version "2.0"
}

licenseReport {
    renderers = arrayOf<ReportRenderer>(CsvReportRenderer())
}

sonarqube {
    properties {
        property("sonar.projectKey", "icure-io_icure-medical-device-kotlin-sdk")
        property("sonar.organization", "icure-io")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}

buildscript {
    repositories {
        mavenCentral()
        maven { url = uri("https://maven.taktik.be/content/groups/public") }
    }
    dependencies {
        classpath("com.taktik.gradle:gradle-plugin-git-version:2.0.2")
        classpath("com.taktik.gradle:gradle-plugin-maven-repository:1.0.2")
        classpath("com.taktik.gradle:gradle-plugin-kt-to-ts:1.0")
    }
}

apply(plugin = "git-version")
apply(plugin = "maven-repository")
apply(plugin = "kt-to-ts")
configure<KtToTsConfig> {
    this.packageToGenerateFiles = "io.icure.md.client.filter.hcp"
    this.pathCreateFiles = "./models-ts/filters/"
}

val gitVersion: String? by project

group = "io.icure"
version = gitVersion ?: "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.taktik.be/content/groups/public")
    }
}

dependencies {
    api(group = "com.github.pozo", name = "mapstruct-kotlin", version = "1.3.1.2")
    kapt(group = "com.github.pozo", name = "mapstruct-kotlin-processor", version = "1.3.1.2")

    implementation(group = "org.jetbrains.kotlin", name = "kotlin-stdlib", version = kotlinVersion)

    implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version = kotlinCoroutinesVersion)
    implementation(
        group = "org.jetbrains.kotlinx",
        name = "kotlinx-coroutines-reactive",
        version = kotlinCoroutinesVersion
    )
    implementation(
        group = "org.jetbrains.kotlinx",
        name = "kotlinx-coroutines-reactor",
        version = kotlinCoroutinesVersion
    )

    implementation(group = "com.fasterxml.jackson.core", name = "jackson-core", version = jacksonVersion)
    implementation(group = "com.fasterxml.jackson.core", name = "jackson-databind", version = jacksonVersion)
    implementation(group = "com.fasterxml.jackson.module", name = "jackson-module-kotlin", version = jacksonVersion)
    implementation(group = "com.fasterxml.jackson.datatype", name = "jackson-datatype-jsr310", version = jacksonVersion)

    implementation(group = "io.icure", name = "async-jackson-http-client", version = "0.1.15-9cf193799d")
    implementation(
        group = "io.icure",
        name = "icure-reactive-kotlin-client",
        version = "0.1.335-884232c19a"
    )
    implementation(group = "org.taktik.commons", name = "commons-uti", version = "1.0")

    implementation(group = "com.github.ben-manes.caffeine", name = "caffeine", version = "3.0.4")

    implementation(group = "javax.inject", name = "javax.inject", version = "1")
    implementation(group = "org.mapstruct", name = "mapstruct", version = "1.3.1.Final")
    implementation(group = "com.github.ben-manes.caffeine", name = "caffeine", version = "3.0.3")

    implementation(group = "ch.qos.logback", name = "logback-classic", version = "1.2.9")
    implementation(group = "ch.qos.logback", name = "logback-access", version = "1.2.6")

    implementation(group = "org.slf4j", name = "slf4j-api", version = "1.7.12")
    implementation(group = "org.slf4j", name = "jul-to-slf4j", version = "1.7.12")
    implementation(group = "org.slf4j", name = "jcl-over-slf4j", version = "1.7.12")
    implementation(group = "org.slf4j", name = "log4j-over-slf4j", version = "1.7.12")

    implementation(group = "io.projectreactor", name = "reactor-core", version = "3.4.10")
    implementation(group = "io.projectreactor.netty", name = "reactor-netty", version = "1.0.11")

    // Bouncy Castle
    implementation(group = "org.bouncycastle", name = "bcprov-jdk15on", version = "1.69")
    implementation(group = "org.bouncycastle", name = "bcmail-jdk15on", version = "1.69")

    testImplementation(group = "io.kotlintest", name = "kotlintest", version = "2.0.7")
    testImplementation(group = "org.junit.jupiter", name = "junit-jupiter", version = "5.7.0")
    testImplementation(group = "com.willowtreeapps.assertk", name = "assertk-jvm", version = "0.24")
    testImplementation(group = "io.icure", name = "diff-utils", version = "0.1.3-959d0c98a9")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.getByName("publish") {
    dependsOn("apiGenerate", "build")
}

tasks.register("apiGenerate", Jar::class) {
    inputs.files(fileTree("openApiTemplates"), File("$rootDir/icure-medical-device-spec.json"))
        .withPropertyName("sourceFiles")
        .withPathSensitivity(PathSensitivity.RELATIVE)
    doLast {
        javaexec {
            main = "-jar"
            args = listOf(
                "$rootDir/openapi-generator-cli.jar",
                "generate",
                "-i",
                "$rootDir/icure-medical-device-spec.json",
                "-g",
                "kotlin",
                "-o",
                "$rootDir",

                "--model-package",
                "io.icure.md.client.models",
                "--api-package",
                "io.icure.md.client.apis",
                "--package-name",
                "io.icure.md.client",
                "--group-id",
                "io.icure",
                "--artifact-id",
                project.name,
                "--artifact-version",
                "0.0.1-SNAPSHOT",
                "--template-dir",
                "$rootDir/openApiTemplates",
                "--additional-properties",
                "useCoroutines=true,serializationLibrary=jackson,sortModelPropertiesByRequiredFlag=false"
            )
        }
    }
    dependsOn.add("download-openapi-spec") // required due to https://github.com/OpenAPITools/openapi-generator/issues/8255
    finalizedBy("apply-custom-fixes", "delete-unused-files")
}

tasks.register("download-openapi-spec") {
    doLast {
        val destFile = File("$rootDir/icure-medical-device-spec.json")
        val url = "${System.getProperty("API_URL")}/v3/api-docs/v2"
        ant.invokeMethod("get", mapOf("src" to url, "dest" to destFile))
    }
}

tasks.register("apply-custom-fixes") {
    doLast {
        // Use manually added filter classes instead of the generated ones
        val replacements = mapOf(
            "io.icure.md.client.infrastructure" to "io.icure.kraken.client.infrastructure",
            "io.icure.md.client.models.Filter" to "io.icure.md.client.filter.Filter"
        )

        // in Folders
        val folders = listOf(
            "$rootDir/src/main/kotlin/io/icure/md/client/apis",
            "$rootDir/docs",
            "$rootDir/src/main/kotlin/io/icure/md/client/models"
        )

        for (folder in folders) {
            for ((match, replace) in replacements) {
                ant.withGroovyBuilder {
                    "replaceregexp"(
                        "match" to "(?<!\\.)$match",
                        "replace" to replace,
                        "flags" to "g",
                        "byline" to "true"
                    ) {
                        "fileset"(
                            "dir" to File(folder)
                        )
                    }
                }
            }
        }
        for (clazz in listOf("DataSample", "Coding", "Patient", "HealthcareElement", "HealthcareProfessional", "MedicalDevice", "User")) {
            ant.withGroovyBuilder {
                "replaceregexp"(
                    "match" to ": Filter(?!<)",
                    "replace" to ": Filter<$clazz>",
                    "flags" to "g",
                    "byline" to "true"
                ) {
                    "fileset"(
                        "file" to File("$rootDir/src/main/kotlin/io/icure/md/client/apis/${clazz}Api.kt")
                    )
                }
            }
        }
    }
}
tasks.create<Delete>("delete-unused-files") {
    delete(
        File("$rootDir/src/main/kotlin/io/icure/md/client/infrastructure"),
        File("$rootDir/src/main/kotlin/io/icure/md/client/models/Filter.kt"),
        File("$rootDir/src/main/kotlin/io/icure/md/client/models/InlineResponse403.kt"),
        File("$rootDir/src/test/resources/parameters"),
        File("$rootDir/src/test/kotlin/io/icure/md/client/apis/CodingApiTest.kt"),
        File("$rootDir/src/test/kotlin/io/icure/md/client/apis/DataSampleApiTest.kt"),
        File("$rootDir/src/test/kotlin/io/icure/md/client/apis/MedicalDeviceApiTest.kt"),
        File("$rootDir/src/test/kotlin/io/icure/md/client/apis/HealthcareElementApiTest.kt"),
        File("$rootDir/src/test/kotlin/io/icure/md/client/apis/HealthcareProfessionalApiTest.kt"),
        File("$rootDir/src/test/kotlin/io/icure/md/client/apis/PatientApiTest.kt"),
        File("$rootDir/src/test/kotlin/io/icure/md/client/apis/UserApiTest.kt")
    )
}

tasks.test {
    useJUnitPlatform()
}

tasks.jacocoTestReport {
    reports {
        xml.isEnabled = true
    }
}

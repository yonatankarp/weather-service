plugins {
    id("com.diffplug.spotless") version libs.versions.spotless 
    id("com.revolut.jooq-docker") version libs.versions.jooq.docker.plugin 
    id("io.spring.dependency-management") version libs.versions.spring.dependency.management 
    id("jacoco")
    id("org.springframework.boot") version libs.versions.springboot 
    id("cat-fact-service.code-metrics")
    id("cat-fact-service.java-conventions")
    id("cat-fact-service.publishing-conventions")
    kotlin("jvm") version libs.versions.kotlin 
    kotlin("plugin.spring") version libs.versions.kotlin 
}

repositories {
    mavenLocal()
    mavenCentral()
    maven { url = uri("https://packages.confluent.io/maven/") }
    maven {
        url = uri("https://maven.pkg.github.com/yonatankarp/cat-fact-service")
        credentials {
            username = findProperty("gpr.user")?.toString() ?: System.getenv("GITHUB_ACTOR")
            password = findProperty("gpr.key")?.toString() ?: System.getenv("GITHUB_TOKEN")
        }
    }
    maven {
        url = uri("https://maven.pkg.github.com/yonatankarp/cat-fact-client")
        credentials {
            username = project.findProperty("gpr.user")?.toString() ?: System.getenv("GITHUB_ACTOR")
            password = project.findProperty("gpr.key")?.toString() ?: System.getenv("PACKAGES_READ_TOKEN")
        }
    }
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.jvmTarget.get()))
        target { JavaLanguageVersion.of(libs.versions.jvmTarget.get()) }
    }
}

dependencies {

    implementation(libs.cat.fact.client)

    // Spring Boot
    implementation(libs.bundles.springboot.all)

    // Kotlin
    implementation(libs.bundles.kotlin.all)

    // Persistence
    runtimeOnly(libs.postgresql)
    jdbc(libs.postgresql) // Required to generate JOOQ models
    implementation(libs.bundles.persistence.support.all)

    // Documentation
    implementation(libs.springdoc.openapi.starter)

    // Observability
    implementation(libs.honeycomb.opentelemetry.sdk)
    compileOnly(libs.honeycomb.opentelemetry.javaagent)

    // Tests
    testImplementation(libs.mockk.core)
    testImplementation(libs.mockk.spring)

    testImplementation(libs.testcontainers.jupiter)
    testImplementation(libs.testcontainers.postgres)
}

dependencyManagement {
    imports {
        mavenBom("org.testcontainers:testcontainers-bom:${libs.versions.testcontainers.get()}")
    }
}

tasks {
    bootRun {
        environment = mapOf(
            "HONEYCOMB_API_KEY" to System.getenv("HONEYCOMB_API_KEY"),
            "SERVICE_NAME" to "cat-fact-service",
            "HONEYCOMB_API_ENDPOINT" to "https://api.honeycomb.io:443",
            "ENVIRONMENT" to "test",
        )

        jvmArgs = listOf(
            "-javaagent:${layout.buildDirectory.get().asFile}/output/libs/honeycomb-opentelemetry-javaagent.jar",
            "-Dotel.resource.attributes=github.repository=https://github.com/yonatankarp/cat-fact-service",
        )
    }

    getByName<Jar>("jar") {
        enabled = false
    }

    build {
        finalizedBy(spotlessApply)
        dependsOn("copyOpenTelemetryAgent")

    }

    withType<Test> {
        useJUnitPlatform()
        finalizedBy(spotlessApply)
        finalizedBy(jacocoTestReport)
    }

    jacoco {
        toolVersion = libs.versions.jacoco.get()
    }

    jacocoTestReport {
        reports {
            xml.required.set(true)
            html.required.set(true)
        }
    }

    register<Copy>("copyOpenTelemetryAgent") {
        project.delete(
            fileTree("${layout.buildDirectory.get().asFile}/output/libs"),
        )

        from(configurations.compileClasspath)
        into("${layout.buildDirectory.get().asFile}/output/libs")
        include("honeycomb-opentelemetry-javaagent*")
        rename("-[1-9]+.[0-9]+.[0-9]+.jar", ".jar")
    }
}

val tasksDependencies = mapOf(
    "spotlessKotlin" to listOf("compileKotlin", "compileTestKotlin", "test", "jacocoTestReport", "copyOpenTelemetryAgent")
)

tasksDependencies.forEach { (taskName, dependencies) ->
    tasks.findByName(taskName)?.dependsOn(dependencies)
}

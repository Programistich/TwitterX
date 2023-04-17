import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.0.4"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version "1.7.22"
    kotlin("plugin.spring") version "1.7.22"
}

group = "com.programistich"
version = "0.0.1"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
    maven { setUrl("https://jitpack.io") }
    maven {
        setUrl("https://takke.github.io/maven")
        content {
            includeGroup("jp.takke.twitter4j-v2")
        }
    }
}

dependencies {
    //  Telegram
    implementation("org.telegram:telegrambots-spring-boot-starter:6.5.0")

    // Twitter
    implementation("jp.takke.twitter4j-v2:twitter4j-v2-support:1.0.3")
    implementation("org.twitter4j:twitter4j-core:4.0.7")
    implementation("org.twitter4j:twitter4j-stream:4.0.7")
    implementation("org.twitter4j:twitter4j-media-support:4.0.6")
    implementation("com.twitter:twitter-api-java-sdk:2.0.3")

    // Database
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    runtimeOnly("org.postgresql:postgresql")
    implementation("org.flywaydb:flyway-core")

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.3")

    // Ktor
    val ktorVersion = "2.2.4"
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-auth:$ktorVersion")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.4.4"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "kometa.workstation.server"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation ("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation ("org.springframework.boot:spring-boot-starter-data-rest")
    implementation ("org.springframework.boot:spring-boot-starter-security")
    implementation ("org.postgresql:postgresql")
    implementation ("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation ("org.springframework.boot:spring-boot-starter-data-redis")
    implementation ("io.lettuce:lettuce-core:6.4.0.RELEASE")
    compileOnly ("org.projectlombok:lombok")
    annotationProcessor ("org.projectlombok:lombok")
    implementation ("io.micrometer:micrometer-registry-prometheus")
    implementation ("org.springframework.boot:spring-boot-starter-actuator")
    implementation ("org.jetbrains.kotlin:kotlin-reflect")
    implementation ("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("io.github.cdimascio:java-dotenv:5.2.2")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")
    testImplementation ("org.springframework.boot:spring-boot-starter-test")
    testImplementation ("org.springframework.security:spring-security-test")
    testRuntimeOnly ("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

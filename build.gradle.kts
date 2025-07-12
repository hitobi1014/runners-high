plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("kapt") version "1.9.25"
    kotlin("plugin.jpa") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    kotlin("plugin.allopen") version "1.9.25"

    id("org.springframework.boot") version "3.4.4"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "io.runnershigh"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

val koVersion = "5.7.2"

dependencies {
    // spring
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.data:spring-data-commons")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Security
    implementation("org.springframework.boot:spring-boot-starter-security")
    testImplementation("org.springframework.security:spring-security-test")

    // JWT 라이브러리 (jjwt - Java JWT)
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")          // JWT API
    implementation("io.jsonwebtoken:jjwt-impl:0.11.5")         // JWT 구현체
    implementation("io.jsonwebtoken:jjwt-jackson:0.11.5")      // Jackson 지원 (JSON 처리용)

    // DB
    runtimeOnly("org.postgresql:postgresql")

    // Querydsl
//    implementation("com.querydsl:querydsl-jpa:5.0.0:jakarta")
//    implementation("com.querydsl:querydsl-core:5.0.0")
//    kapt("com.querydsl:querydsl-apt:5.0.0:jakarta")
    implementation("com.querydsl:querydsl-jpa:5.0.0:jakarta")
    implementation("com.querydsl:querydsl-apt:5.0.0:jakarta")
    implementation("jakarta.persistence:jakarta.persistence-api")
    implementation("jakarta.annotation:jakarta.annotation-api")
    kapt("com.querydsl:querydsl-apt:5.0.0:jakarta")
    kapt("jakarta.persistence:jakarta.persistence-api")
    kapt("jakarta.annotation:jakarta.annotation-api")
    kapt("org.springframework.boot:spring-boot-configuration-processor")

    // Querydsl Logging
    implementation("com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.9.0")

    // Development Tools
    kapt("org.springframework.boot:spring-boot-configuration-processor")

    // Logging
    implementation("org.springframework.boot:spring-boot-starter-logging") // 스프링 부트 로깅 스타터
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5") // 코틀린 로깅 라이브러리

    // validation
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "mockito-core") // Mockito 제외
    }
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("com.h2database:h2")

    // MockK 관련 라이브러리
    testImplementation("io.mockk:mockk:1.13.9") // MockK 코어 라이브러리
    testImplementation("com.ninja-squad:springmockk:4.0.2") // Spring 통합용 MockK

    // kotest
    testImplementation("io.kotest:kotest-runner-junit5:$koVersion")
    testImplementation("io.kotest:kotest-assertions-core:$koVersion")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.3")

    // dummy data 생성
    implementation("net.datafaker:datafaker:2.4.2")
}

kotlin {
    jvmToolchain(17)
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

noArg {
    annotation("jakarta.persistence.Entity")
}

// build.gradle.kts
sourceSets {
    main {
        java {
            srcDirs("src/main/java", "src/main/kotlin", "build/generated/source/kapt/main")
        }
    }
}

kapt {
    correctErrorTypes = true
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.jar {
    archiveBaseName.set("app") // JAR -> app
    archiveVersion.set("") // 버전 번호 x
}

tasks.bootJar {
    archiveBaseName.set("app") // JAR 파일명 app
    archiveVersion.set("") // 버전 번호 x
    archiveClassifier.set("") // plain 접미사 제거
}
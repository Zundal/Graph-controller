plugins {
    java
    id("org.springframework.boot") version "3.3.3"
    id("io.spring.dependency-management") version "1.1.6"
}

group = "com.flexsible"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("org.mariadb:r2dbc-mariadb:1.1.4")
    implementation ("org.apache.tinkerpop:gremlin-core:3.5.0")
    implementation ("org.apache.tinkerpop:tinkergraph-gremlin:3.5.0")
    implementation ("org.apache.tinkerpop:gremlin-driver:3.5.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

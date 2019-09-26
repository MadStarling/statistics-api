plugins {
    java
    application
    id("org.springframework.boot") version "2.1.4.RELEASE"
    id("io.spring.dependency-management") version "1.0.7.RELEASE"
}

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    implementation("com.google.guava:guava:27.1-jre")
    implementation("org.springframework.boot:spring-boot-starter-web:2.1.8.RELEASE")

    testImplementation("org.springframework.boot:spring-boot-starter-test:2.1.8.RELEASE")
    testImplementation("org.junit.jupiter:junit-jupiter:5.4.2")
}

application {
    mainClassName = "statistics.api"
}

tasks.withType<Test> {
    useJUnitPlatform ()
}
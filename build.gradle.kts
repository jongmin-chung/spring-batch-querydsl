plugins {
    java
    id("org.springframework.boot") version "3.5.3"
    id("io.spring.dependency-management") version "1.1.7"
}

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")

    group = "io.github.jongminchung"
    version = "3.0.0"

    java.toolchain.languageVersion = JavaLanguageVersion.of(21)
    java.sourceCompatibility = JavaVersion.VERSION_21
    java.targetCompatibility = JavaVersion.VERSION_21

    dependencyManagement {
        imports {
            mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        systemProperty("spring.profiles.active", "test")
    }

    configurations {
        compileOnly {
            extendsFrom(configurations.annotationProcessor.get())
        }
    }
}

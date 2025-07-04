plugins {
	java
	id("org.springframework.boot")
	id("io.spring.dependency-management")
	id("com.vanniktech.maven.publish") version "0.33.0"
	signing
}

tasks {
	jar { enabled = true }
	bootJar { enabled = false }
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-batch")
	val querydsl = "7.0"
	implementation("io.github.openfeign.querydsl:querydsl-core:${querydsl}")
	implementation("io.github.openfeign.querydsl:querydsl-jpa:$querydsl")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.batch:spring-batch-test")
}

mavenPublishing {
	coordinates(group.toString(), project.name, version.toString())

	pom {
		name = project.name
		description = "Querydsl ItemReader For Spring Batch."
		url = "http://github.com/jongmin-chung/spring-batch-querydsl"

		licenses {
			license {
				name = "MIT License"
				url = "https://opensource.org/license/mit/"
			}
		}

		developers {
			developer {
				id = "jongmin-chung"
				name = "Jongmin Chung"
				email = "chungjm0711@gmail.com"
				url = "https://github.com/jongmin-chung"
			}
		}

		scm {
			connection = "scm:git:git:github.com/jongmin-chung/spring-batch-querydsl.git"
			developerConnection = "scm:git:ssh://github.com/jongmin-chung/spring-batch-querydsl.git"
			url = "http://github.com/jongmin-chung/spring-batch-querydsl"
		}
	}

	signAllPublications()
}

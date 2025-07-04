plugins {
	java
	id("org.springframework.boot")
	id("io.spring.dependency-management")
}

dependencies {
	implementation(project(":spring-batch-querydsl-reader", configuration = "default"))

	implementation("org.springframework.boot:spring-boot-starter-batch")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	runtimeOnly("com.h2database:h2")
	implementation("org.mariadb.jdbc:mariadb-java-client")

	val querydsl = "7.0"
	implementation("io.github.openfeign.querydsl:querydsl-core:${querydsl}")
	implementation("io.github.openfeign.querydsl:querydsl-jpa:$querydsl")
	annotationProcessor("io.github.openfeign.querydsl:querydsl-apt:$querydsl:jpa")

	annotationProcessor("org.projectlombok:lombok")
	implementation("org.projectlombok:lombok")
	testAnnotationProcessor("org.projectlombok:lombok")
	testImplementation("org.projectlombok:lombok")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.batch:spring-batch-test")
}

sourceSets {
	main {
		java {
			srcDirs("src/main/java", "${layout.buildDirectory}/generated")
		}
	}
}

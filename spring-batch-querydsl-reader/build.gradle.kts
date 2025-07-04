tasks {
	jar {
		enabled = true
	}
	bootJar {
		enabled = false
	}
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

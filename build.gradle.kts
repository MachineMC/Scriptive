plugins {
    java
    `maven-publish`
}

group = "org.machinemc"
version = "1.2"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.code.gson:gson:2.10.1")
    compileOnly("org.jetbrains:annotations:24.0.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    withSourcesJar()
}

publishing {
    repositories {
        maven {
            name = "machine"
            url = uri("https://repo.machinemc.org/releases")
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = "org.machinemc"
            artifactId = "scriptive"
            version = project.version.toString()
            from(components["java"])
        }
    }
}

tasks {
    compileJava {
        options.release.set(21)
        options.encoding = Charsets.UTF_8.name()
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name()
    }
    test {
        useJUnitPlatform()
    }
}

plugins {
    id("java-library-convention")
    `maven-publish`
}

repositories {
    maven("https://repo.machinemc.org/releases")
}

dependencies {
    implementation(project(":scriptive-core"))
    implementation(libs.machinemc.nbt)

    testImplementation(project(":scriptive-gson"))
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
            artifactId = "scriptive-nbt"
            version = project.version.toString()
            from(components["java"])
        }
    }
}
plugins {
    id("java")
    id("application")
}

group = "socketmail"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("socketmail.Main")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "socketmail.Main"
        )
    }
}

tasks.test {
    useJUnitPlatform()
}
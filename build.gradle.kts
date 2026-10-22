plugins {
    id("java")
    id("org.jetbrains.intellij.platform") version "2.2.1"
}

group = "com.atagon.cryptography"
version = "1.1.0"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        intellijIdeaCommunity("2024.2.4")
        bundledPlugin("com.intellij.java")
    }
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.2")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks {
    patchPluginXml {
        sinceBuild.set("242")
        untilBuild.set(provider { null })
    }
    test {
        useJUnitPlatform()
    }
}

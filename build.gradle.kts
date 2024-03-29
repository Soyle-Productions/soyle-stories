
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version plugin.constants.kotlinVersion
    id(plugin.constants.detekt) version plugin.constants.detektVersion
}

java {
    version = plugin.constants.javaVersion
}

repositories {
    mavenCentral()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = plugin.constants.javaVersion
}

allprojects {

    group = "com.soyle.stories"
    version = "0.13.0 Pre-Release 3"

    tasks.withType<Jar>().forEach {
        it.archiveBaseName.set(properties["archive-name"] as? String ?: name)
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = plugin.constants.javaVersion
        kotlinOptions.useIR = true
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}


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
    version = "0.14 SNAPSHOT 21w37a"

    tasks.withType<Jar>().forEach {
        it.archiveBaseName.set(properties["archive-name"] as? String ?: name)
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = plugin.constants.javaVersion
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

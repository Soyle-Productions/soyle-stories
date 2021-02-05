import librarires.Libraries

plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":core"))
    implementation(Libraries.kotlin.coroutines)

    implementation(project(":desktop:application"))
}
import librarires.Libraries

plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":usecases"))
    implementation(Libraries.kotlin.coroutines)

    implementation(project(":desktop:application"))
}
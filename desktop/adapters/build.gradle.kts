import librarires.Libraries

plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(Libraries.kotlin.coroutines)
    implementation(project(":core:usecases"))
    implementation(project(":desktop:application"))

    testImplementation(Libraries.junit.api)
    testImplementation(Libraries.junit.engine)
}
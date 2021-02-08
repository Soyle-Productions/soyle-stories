import librarires.Libraries

plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":usecases"))

    implementation("io.arrow-kt:arrow-core:0.10.4")
    implementation("io.arrow-kt:arrow-core-data:0.10.4")

    implementation(Libraries.kotlin.coroutines)

    testImplementation(Libraries.junit.params)
    implementation("de.jodamob.junit5:junit5-kotlin:0.0.1")
}
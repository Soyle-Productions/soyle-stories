import librarires.Libraries

plugins {
    kotlin("jvm")
    id(plugin.constants.detekt)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":core:usecases"))

    implementation("io.arrow-kt:arrow-core:0.10.4")
    implementation("io.arrow-kt:arrow-core-data:0.10.4")

    implementation(Libraries.kotlin.coroutines)

    testImplementation(Libraries.junit.params)
    testImplementation(testFixtures(project(path = ":core:usecases")))
    implementation("de.jodamob.junit5:junit5-kotlin:0.0.1")
}
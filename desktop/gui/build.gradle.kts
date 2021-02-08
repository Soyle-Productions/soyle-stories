import librarires.Libraries

plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":usecases"))
    implementation( project(":desktop:adapters"))
    implementation( project(":desktop:application"))
    implementation( Libraries.kotlin.coroutines)
    testImplementation( Libraries.assertJ)

    implementation (Libraries.kotlin.reflection)
}
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
    implementation( project(":desktop:adapters"))
    implementation( project(":desktop:application"))
    implementation( Libraries.kotlin.coroutines.core)
    testImplementation( Libraries.assertJ)

    implementation (Libraries.kotlin.reflection)
}
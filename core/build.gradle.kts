import librarires.Libraries

plugins {
    kotlin("jvm")
    id(plugin.constants.ideaExt) version plugin.constants.ideaExtVersion
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(Libraries.kotlin.coroutines)
    implementation("io.arrow-kt:arrow-core:0.10.4")
    implementation( "io.arrow-kt:arrow-core-data:0.10.4")

    testImplementation(Libraries.junit.api)
    testImplementation(Libraries.junit.engine)
    testImplementation(Libraries.junit.params)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

idea {
    module {
        (this as ExtensionAware).configure<org.jetbrains.gradle.ext.ModuleSettings> {
            (this as ExtensionAware).the<org.jetbrains.gradle.ext.PackagePrefixContainer>()["src/main/kotlin"] = "com.soyle.stories"
            (this as ExtensionAware).the<org.jetbrains.gradle.ext.PackagePrefixContainer>()["src/test/kotlin"] = "com.soyle.stories"
        }
    }
}
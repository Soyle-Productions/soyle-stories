import librarires.Libraries

repositories {
    mavenCentral()
}

plugins {
    kotlin("jvm")
    id(plugin.constants.ideaExt) version plugin.constants.ideaExtVersion
    `java-test-fixtures`
}

dependencies {
    implementation(Libraries.kotlin.coroutines)
    implementation("io.arrow-kt:arrow-core:0.10.4")
    implementation( "io.arrow-kt:arrow-core-data:0.10.4")

    testImplementation(Libraries.junit.api)
    testImplementation(Libraries.junit.engine)

    testFixturesApi( Libraries.junit.api)
    testFixturesApi( Libraries.junit.engine)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

idea {
    module {
        (this as ExtensionAware).configure<org.jetbrains.gradle.ext.ModuleSettings> {
            (this as ExtensionAware).the<org.jetbrains.gradle.ext.PackagePrefixContainer>()["src/main/kotlin"] = "com.soyle.stories.domain"
            (this as ExtensionAware).the<org.jetbrains.gradle.ext.PackagePrefixContainer>()["src/test/kotlin"] = "com.soyle.stories.domain"
            (this as ExtensionAware).the<org.jetbrains.gradle.ext.PackagePrefixContainer>()["src/testFixtures/kotlin"] = "com.soyle.stories.domain"
        }
    }
}
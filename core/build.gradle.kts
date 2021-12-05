plugins {
    java
    kotlin("jvm")
    id(plugin.constants.ideaExt) version plugin.constants.ideaExtVersion
    `java-test-fixtures`
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    testFixturesImplementation(librarires.Libraries.kotlin.coroutines.core)
    testFixturesImplementation(librarires.Libraries.kluent)
    testFixturesApi(testFixtures(project(path = ":core:domain")))
    testFixturesApi(project(path = ":core:usecases"))
    testFixturesApi(testFixtures(project(path = ":core:usecases")))
}

tasks.test {
    useJUnitPlatform()
}

tasks.getByName("check") {
    dependsOn(":core:usecases:check", ":core:domain:check")
    mustRunAfter(":core:usecases:check", ":core:domain:check")
}

idea {
    module {
        (this as ExtensionAware).configure<org.jetbrains.gradle.ext.ModuleSettings> {
            (this as ExtensionAware).the<org.jetbrains.gradle.ext.PackagePrefixContainer>()["src/test/kotlin"] = "com.soyle.stories.core"
            (this as ExtensionAware).the<org.jetbrains.gradle.ext.PackagePrefixContainer>()["src/testFixtures/kotlin"] = "com.soyle.stories.core"
        }
    }
}
import librarires.Libraries

plugins {
    kotlin("jvm")
    id(plugin.constants.detekt)
    `java-test-fixtures`
    id(plugin.constants.ideaExt)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(Libraries.kotlin.coroutines.core)
    implementation(project(":core:usecases"))
    implementation(project(":desktop:application"))

    testImplementation(Libraries.junit.api)
    testImplementation(Libraries.junit.engine)

    testFixturesImplementation(Libraries.kotlin.coroutines.core)
    testFixturesImplementation(project(":core:usecases"))
}


idea {
    module {
        this as ExtensionAware
        configure<org.jetbrains.gradle.ext.ModuleSettings> {
            this as ExtensionAware
            val packagePrefix = "com.soyle.stories.desktop.adapter"

            the<org.jetbrains.gradle.ext.PackagePrefixContainer>()["src/testFixtures/kotlin"] = packagePrefix
        }
    }
}
plugins {
    kotlin("jvm")
    id(plugin.constants.detekt)
    id(plugin.constants.ideaExt)
    id("org.jetbrains.compose") version "1.0.1"
}

dependencies {

    api( project(":core:usecases"))
    api( project(":desktop:application"))
    api( project(":desktop:adapters"))
    api (librarires.Libraries.kotlin.coroutines.core)
    implementation("io.insert-koin:koin-core:3.1.4")
    implementation(compose.desktop.currentOs)

    @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
    implementation(compose.desktop.components.splitPane)

    testImplementation( librarires.Libraries.kotlin.reflection)
    testImplementation( librarires.Libraries.junit.api)
    testImplementation( librarires.Libraries.junit.engine)
    testImplementation( librarires.Libraries.junit.params)
    testImplementation(librarires.Libraries.kotlin.coroutines.test)
    testImplementation(testFixtures(project(":desktop:adapters")))

}

tasks.processResources {
    filesMatching("**/*.properties") {
        expand("APPLICATION_VERSION" to project.version)
    }
}

tasks.withType<CreateStartScripts> {
    doFirst {
        classpath =  files("lib/*")
    }
}

idea {
    module {
        this as ExtensionAware
        configure<org.jetbrains.gradle.ext.ModuleSettings> {
            this as ExtensionAware
            val packagePrefix = "com.soyle.stories.desktop.view"

            the<org.jetbrains.gradle.ext.PackagePrefixContainer>()["src/main/kotlin"] = packagePrefix
            the<org.jetbrains.gradle.ext.PackagePrefixContainer>()["src/test/kotlin"] = packagePrefix
        }
    }
}
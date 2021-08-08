import librarires.Libraries

repositories {
    mavenCentral()
}

plugins {
    kotlin("jvm")
    id(plugin.constants.detekt)
    id(plugin.constants.ideaExt) version plugin.constants.ideaExtVersion
    `java-test-fixtures`
}

val integrationTest = sourceSets.create("integrationTest") {
    withConvention(org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet::class) {
        kotlin.srcDir("src/integrationTest/kotlin")
        compileClasspath += sourceSets["main"].output + configurations["testRuntimeClasspath"]
        runtimeClasspath += output + compileClasspath + sourceSets["testFixtures"].runtimeClasspath

        idea {
            module {
                testSourceDirs.addAll(kotlin.srcDirs)
            }
        }

    }
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

task<Test>("integrationTest") {
    description = "Runs the integration tests"
    group = "verification"
    testClassesDirs = sourceSets["integrationTest"].output.classesDirs
    classpath = sourceSets["integrationTest"].runtimeClasspath
    mustRunAfter(tasks["test"])
    tasks.check.get().dependsOn(this)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

idea {
    module {
        this as ExtensionAware
        configure<org.jetbrains.gradle.ext.ModuleSettings> {
            this as ExtensionAware
            the<org.jetbrains.gradle.ext.PackagePrefixContainer>()["src/main/kotlin"] = "com.soyle.stories.domain"
            the<org.jetbrains.gradle.ext.PackagePrefixContainer>()["src/test/kotlin"] = "com.soyle.stories.domain"
            the<org.jetbrains.gradle.ext.PackagePrefixContainer>()["src/integrationTest/kotlin"] = "com.soyle.stories.domain"
            the<org.jetbrains.gradle.ext.PackagePrefixContainer>()["src/testFixtures/kotlin"] = "com.soyle.stories.domain"
        }
    }
}
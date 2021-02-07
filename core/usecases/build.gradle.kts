import librarires.Libraries

plugins {
    kotlin("jvm")
    id(plugin.constants.ideaExt) version plugin.constants.ideaExtVersion
    `java-test-fixtures`
}

repositories {
    mavenCentral()
}

val integrationTest = sourceSets.create("integrationTest") {
    withConvention(org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet::class) {
        kotlin.srcDir("src/integrationTest/kotlin")
        compileClasspath += sourceSets["main"].output + configurations["testRuntimeClasspath"]
        runtimeClasspath += output + compileClasspath + sourceSets["testFixtures"].runtimeClasspath
    }
}

dependencies {
    api(project(":domain"))
    implementation(Libraries.kotlin.coroutines)
    implementation("io.arrow-kt:arrow-core:0.10.4")
    implementation( "io.arrow-kt:arrow-core-data:0.10.4")

    testImplementation(Libraries.junit.api)
    testImplementation(Libraries.junit.engine)
    testImplementation(Libraries.junit.params)
    testImplementation(testFixtures(project(path = ":domain")))
}

task<Test>("integrationTest") {
    description = "Runs the integration tests"
    group = "verification"
    testClassesDirs = sourceSets["integrationTest"].output.classesDirs
    classpath = sourceSets["integrationTest"].runtimeClasspath
    mustRunAfter(tasks["test"])
}

tasks.withType<Test> {
    useJUnitPlatform()
}

idea {
    module {
        (this as ExtensionAware).configure<org.jetbrains.gradle.ext.ModuleSettings> {
            (this as ExtensionAware).the<org.jetbrains.gradle.ext.PackagePrefixContainer>()["src/main/kotlin"] = "com.soyle.stories.usecase"
            (this as ExtensionAware).the<org.jetbrains.gradle.ext.PackagePrefixContainer>()["src/test/kotlin"] = "com.soyle.stories.usecase"
            (this as ExtensionAware).the<org.jetbrains.gradle.ext.PackagePrefixContainer>()["src/integrationTest/kotlin"] = "com.soyle.stories.usecase"
            (this as ExtensionAware).the<org.jetbrains.gradle.ext.PackagePrefixContainer>()["src/testFixtures/kotlin"] = "com.soyle.stories.usecase"
        }
    }
}
import librarires.Libraries

plugins {
    kotlin("jvm")
    id(plugin.constants.detekt)
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
    api(project(":core:domain"))
    implementation(Libraries.kotlin.coroutines.core)
    implementation("io.arrow-kt:arrow-core:0.10.4")
    implementation( "io.arrow-kt:arrow-core-data:0.10.4")

    testImplementation(Libraries.junit.api)
    testImplementation(Libraries.junit.engine)
    testImplementation(Libraries.junit.params)
    testImplementation(testFixtures(project(path = ":core:domain")))

    testFixturesImplementation(Libraries.junit.api)
    testFixturesImplementation(Libraries.kotlin.coroutines.core)
}

tasks.getByName<Test>("test") {
    reports.junitXml.isEnabled = false
    reports.html.isEnabled = false
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
        testSourceDirs.addAll(integrationTest.java.srcDirs)
        integrationTest.withConvention(org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet::class) {
            testSourceDirs.addAll(kotlin.srcDirs)
        }
        (this as ExtensionAware).configure<org.jetbrains.gradle.ext.ModuleSettings> {
            (this as ExtensionAware).the<org.jetbrains.gradle.ext.PackagePrefixContainer>()["src/main/kotlin"] = "com.soyle.stories.usecase"
            (this as ExtensionAware).the<org.jetbrains.gradle.ext.PackagePrefixContainer>()["src/test/kotlin"] = "com.soyle.stories.usecase"
            (this as ExtensionAware).the<org.jetbrains.gradle.ext.PackagePrefixContainer>()["src/integrationTest/kotlin"] = "com.soyle.stories.usecase"
            (this as ExtensionAware).the<org.jetbrains.gradle.ext.PackagePrefixContainer>()["src/testFixtures/kotlin"] = "com.soyle.stories.usecase"
        }
    }
}
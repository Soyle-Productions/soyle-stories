
import librarires.Libraries
import org.openjfx.gradle.JavaFXModule
import org.openjfx.gradle.JavaFXOptions
import org.openjfx.gradle.JavaFXPlatform

plugins {
    kotlin("jvm")
    id(plugin.constants.detekt)
    id(plugin.constants.javafx) version plugin.constants.javaFxVersion
    id(plugin.constants.ideaExt)
    id("java-test-fixtures")
}

sourceSets {
    testFixtures {
        java {
            srcDir("src/testFixtures/kotlin")
        }
    }
}

val design by sourceSets.creating {
    withConvention(org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet::class) {
        kotlin.srcDir("src/design/kotlin")
        compileClasspath += sourceSets["main"].output + configurations["testRuntimeClasspath"]
        runtimeClasspath += output + compileClasspath + sourceSets["testFixtures"].runtimeClasspath

        idea {
            module {
                testSourceDirs.addAll(kotlin.srcDirs)
            }
        }

    }
}

javafx {
    version = "14"
    modules = listOf("javafx.controls", "javafx.fxml"/*, 'javafx.web', 'javafx.swing'*/)
    configuration = "compileOnly"
}

val javaFXOptions = convention.findByType<JavaFXOptions>()
        ?: convention.findPlugin<JavaFXOptions>()
        ?: convention.getByType<JavaFXOptions>()

dependencies {

    // use of api is temporary until more things are moved to root config project
    api( project(":core:usecases"))
    api( project(":desktop:application"))
    api( project(":desktop:adapters"))
    api( project(":desktop:gui"))
    api( "no.tornado:tornadofx:1.7.20") {
        exclude(group = "org.jetbrains.kotlin")
    }
    api (Libraries.kotlin.coroutines.core)
    api ("org.jetbrains.kotlinx:kotlinx-coroutines-javafx:1.4.2")
    implementation( "org.controlsfx:controlsfx:11.0.2")
    implementation( "org.fxmisc.richtext:richtextfx:0.10.5")
    implementation( "no.tornado:tornadofx-controlsfx:0.1")

    implementation( "de.jensd:fontawesomefx-commons:11.0")
    implementation( "de.jensd:fontawesomefx-controls:11.0")
    implementation( "de.jensd:fontawesomefx-fontawesome:4.7.0-11")
    implementation( "de.jensd:fontawesomefx-materialicons:2.2.0-11")
    implementation( "de.jensd:fontawesomefx-emojione:2.2.7-11")

    testImplementation( Libraries.kotlin.reflection)
    testImplementation( Libraries.junit.api)
    testImplementation( Libraries.junit.engine)
    testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-javafx:1.4.2")
    testImplementation(Libraries.kotlin.coroutines.test)

    testFixturesApi( Libraries.junit.api)
    testFixturesApi( Libraries.junit.engine)
    testFixturesApi( Libraries.assertJ)
    testFixturesApi( "org.fxmisc.richtext:richtextfx:0.10.5")
    testFixturesApi( "org.testfx:testfx-core:4.0.16-alpha")
    testFixturesApi( "org.testfx:testfx-junit5:4.0.16-alpha")
    testFixturesApi( "org.testfx:openjfx-monocle:jdk-12.0.1+2")

    val designImplementation by configurations.getting {  }
    val designRuntimeOnly by configurations.getting {  }

    designImplementation( Libraries.junit.api)
    designRuntimeOnly( Libraries.junit.engine)
    designImplementation( Libraries.assertJ)
    designImplementation( "org.testfx:testfx-core:4.0.16-alpha")
    designImplementation( "org.testfx:testfx-junit5:4.0.16-alpha")

    JavaFXPlatform.values().forEach { platform ->
        val cfg = configurations.create("javafx_" + platform.classifier)
        JavaFXModule.getJavaFXModules(javaFXOptions.modules).forEach { m ->
            val dependency = String.format("org.openjfx:%s:%s:%s", m.artifactName, javaFXOptions.version, platform.classifier)
            project.dependencies.add(cfg.name, dependency)
            project.dependencies.add("api", dependency)
        }
    }
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

tasks.create<Test>("design") {
    description = "Validates the Design of the Views"
    group = "verification"
    testClassesDirs = design.output.classesDirs
    classpath = design.runtimeClasspath
    outputs.upToDateWhen { false }
    mustRunAfter(tasks["test"])
    useJUnitPlatform()
}

project.parent?.tasks?.getByName("runtime")?.doLast {
    JavaFXPlatform.values().forEach { platform ->
        val cfg = configurations["javafx_" + platform.classifier]
        cfg.resolvedConfiguration.files.forEach { f ->
            copy {
                from(f)
                into("../build/image/soyle-stories-desktop-${platform.classifier}/lib")
            }
        }
    }
}


idea {
    module {
        this as ExtensionAware
        configure<org.jetbrains.gradle.ext.ModuleSettings> {
            this as ExtensionAware
            val packagePrefix = "com.soyle.stories.desktop.view"

            the<org.jetbrains.gradle.ext.PackagePrefixContainer>()["src/test/kotlin"] = packagePrefix
            the<org.jetbrains.gradle.ext.PackagePrefixContainer>()["src/design/kotlin"] = packagePrefix
            the<org.jetbrains.gradle.ext.PackagePrefixContainer>()["src/testFixtures/kotlin"] = packagePrefix
        }
    }
}
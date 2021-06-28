plugins {
    java
    kotlin("jvm")
    id(plugin.constants.ideaExt) version plugin.constants.ideaExtVersion
    id(plugin.constants.shadow) version plugin.constants.shadowVersion
    id(plugin.constants.badassRuntime) version plugin.constants.badassRuntimeVersion
}

allprojects {
    repositories {
        jcenter()
        mavenCentral()
    }
}

application {
    mainClassName = "com.soyle.stories.desktop.config.soylestories.MainKt"
    applicationName = "Soyle Stories"
}


dependencies {
    implementation(project(":desktop:views"))

    testImplementation("io.cucumber:cucumber-java8:6.1.1")
    testImplementation("io.cucumber:cucumber-junit:6.1.1")

    testImplementation("org.testfx:testfx-core:4.0.16-alpha")
    testImplementation("org.testfx:openjfx-monocle:jdk-12.0.1+2")
    testImplementation(librarires.Libraries.junit.api)

    testImplementation(testFixtures(project(path = ":desktop:views"))) {
        if (this is ModuleDependency) {
            exclude(group = "com.soyle.stories")
        }
    }
}

runtime {
    options.set(listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages"))
    modules.set(
        listOf(
            "java.desktop",
            "java.logging",
            "java.prefs",
            "java.xml",
            "jdk.unsupported",
            "java.scripting",
            "jdk.jfr"
        )
    )

    targetPlatform("linux", System.getenv("JAVA_HOME"))
    targetPlatform("mac", System.getenv("JAVA_HOME"))
    targetPlatform("win", System.getenv("JAVA_HOME"))
}

tasks.withType<CreateStartScripts> {
    doFirst {
        classpath = files("lib/*")
    }
}

tasks.processResources {
    filesMatching("**/*.properties") {
        expand("APPLICATION_VERSION" to project.version)
    }
}

idea {
    module {
        (this as ExtensionAware).configure<org.jetbrains.gradle.ext.ModuleSettings> {
            (this as ExtensionAware).the<org.jetbrains.gradle.ext.PackagePrefixContainer>()["src/main/kotlin"] = "com.soyle.stories.desktop.config"
            (this as ExtensionAware).the<org.jetbrains.gradle.ext.PackagePrefixContainer>()["src/test/kotlin"] = "com.soyle.stories.desktop.config"
        }
    }
}
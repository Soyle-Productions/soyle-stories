import plugin.constants.kotlinVersion

plugins {
    java
    kotlin("jvm")
    id(plugin.constants.detekt)
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
    implementation(project(":desktop:locale"))
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

// in kotlin 1.5+, lambdas provided as SAM interfaces are compiled without a backing class
// This causes the cucumber-java8 framework to fail because it can't handle lambdas without a backing class
// by adding Xsam-conversions = class as a compiler argument for the test code compilation,
// we increase the resulting code size, but it only affects the test code, and we can continue to use
// lambdas and cucumber-java8 instead of changing EVERY step definition to an annotated function with cucumber-java
val compileTestKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    freeCompilerArgs += "-Xsam-conversions=class"
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

    //targetPlatform("linux", System.getenv("JAVA_HOME"))
    //targetPlatform("mac", System.getenv("JAVA_HOME"))
    targetPlatform("win", System.getenv("JAVA_HOME"))

    jpackage {
        resourceDir = File("$buildDir/resources")
        imageOptions = listOf(
            "--icon", "\"Soyle Stories.ico\""
        )
        /*
        Windows limits us to only three dots in the appVersion and a max of 255 for the first two numbers.  Max of 65,535
        for the third number.  In order for incremental builds to take advantage of windows' automatic updating, it must
        always be a higher version number, even if it's just another build of the same release candidate.  So, we'll
        use the same major and minor numbers as the branded version, but each build number will increment and will
        typically be out of sync with the branded version.
         */
        appVersion = "0.13.03"
        imageName = application.applicationName
        installerName = application.applicationName
        installerOptions = listOf(
            "--win-menu",
            "--win-shortcut",
            "--win-dir-chooser",
            "--win-menu-group", "Soyle Studio",
            "--description", "\"Manage your story with ease.\"",
            "--vendor", "\"Soyle Productions\""
        )
    }
}

tasks.getByName("jpackageImage").doLast {
    copy {
        from("src/main/resources")
        include("Soyle Stories.ico")
        into("$buildDir/jpackage/${application.applicationName}")
    }
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
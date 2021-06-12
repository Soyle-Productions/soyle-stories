import librarires.Libraries

plugins {
    kotlin("jvm")
    id(plugin.constants.ideaExt)
}

idea {
    module {
        (this as ExtensionAware).configure<org.jetbrains.gradle.ext.ModuleSettings> {
            (this as ExtensionAware).the<org.jetbrains.gradle.ext.PackagePrefixContainer>()["src/test/kotlin"] = "com.soyle.stories.desktop.locale"
            (this as ExtensionAware).the<org.jetbrains.gradle.ext.PackagePrefixContainer>()["src/testFixtures/kotlin"] = "com.soyle.stories.desktop.locale"
        }
    }
}
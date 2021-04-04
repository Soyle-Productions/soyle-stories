package librarires

object Libraries {

    val kotlin: KotlinLibraries = object : KotlinLibraries {
        override val version = "1.4.30"
        private val coroutinesVersion = "1.4.2"

        override val std = "org.jetbrains.kotlin:kotlin-stdlib:$version"
        override val reflection = "org.jetbrains.kotlin:kotlin-reflect:$version"
        override val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion"
    }
    val junit: JUnitLibraries = object : JUnitLibraries {
        private val version = "5.6.0"

        override val api = "org.junit.jupiter:junit-jupiter-api:$version"
        override val engine = "org.junit.jupiter:junit-jupiter-engine:$version"
        override val params = "org.junit.jupiter:junit-jupiter-params:$version"
    }
    val assertJ = "org.assertj:assertj-core:3.15.0"

}
package librarires

interface KotlinLibraries {
    val version: String
    val std: String
    val reflection: String
    @Deprecated("Moved to coroutines.core", ReplaceWith("this.coroutines.core"))
    val coroutinesCore: String
    val coroutines: CoroutineLibraries
}
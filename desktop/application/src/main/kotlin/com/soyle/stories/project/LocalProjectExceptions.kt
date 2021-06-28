package com.soyle.stories.project

abstract class LocalProjectException : Exception()

class DirectoryDoesNotExist(val directory: String) : LocalProjectException()
class FileAlreadyExists(val existingFilePath: String) : LocalProjectException()
class ProjectFailure(override val cause: Throwable) : LocalProjectException() {
    override val message: String?
        get() = cause.message

    override fun getLocalizedMessage(): String {
        return cause.localizedMessage
    }

    override fun toString(): String {
        return "ProjectFailure -> $cause"
    }
}
class NeverOpenedProject() : LocalProjectException()
class NeverStartedNewProject() : LocalProjectException()
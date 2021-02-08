package com.soyle.stories.project.usecases

import com.soyle.stories.domain.project.Project
import com.soyle.stories.project.DirectoryDoesNotExist
import com.soyle.stories.project.FileAlreadyExists
import com.soyle.stories.project.LocalProjectException
import com.soyle.stories.project.ProjectFailure
import com.soyle.stories.usecase.project.startNewProject.StartNewProject
import com.soyle.stories.project.usecases.startnewLocalProject.StartNewLocalProject
import com.soyle.stories.project.usecases.startnewLocalProject.StartNewLocalProjectUseCase
import com.soyle.stories.workspace.ProjectException
import com.soyle.stories.workspace.repositories.FileRepository
import com.soyle.stories.workspace.usecases.closeProject.CloseProject
import com.soyle.stories.workspace.usecases.openProject.OpenProject
import com.soyle.stories.workspace.valueobjects.ProjectFile
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import java.util.*

@Timeout(3)
class StartNewLocalProjectUnitTest {

    @Test
    fun `Directory does not exist`() {
        given {
            directoryDoesNotExist()
        }
        whenUseCaseIsExecuted()
        then {
            output as DirectoryDoesNotExist
            output.directory.mustEqual(requestDirectory) { "Output directory does not equal request directory" }
        }
    }

    @Test
    fun `File with provided name exists in directory`() {
        given {
            directoryExists()
            fileWithNameExists()
        }
        whenUseCaseIsExecuted()
        then {
            output as FileAlreadyExists
            output.existingFilePath.mustEqual("$requestDirectory\\$requestFileName.stry") {
                "Existing file path does not match expected file path"
            }
        }
    }

    @Test
    fun `Start new project failures should be output`() {
        val errorThrown = Error("Test error from base use case")
        given {
            startNewProjectUseCaseWillThrowError(errorThrown)
        }
        whenUseCaseIsExecuted()
        then {
            output as ProjectFailure
            assertEquals(errorThrown, output.cause)
        }
    }

    @Test
    fun `Start new project success should create new file`() {
        given {
            directoryExists()
            fileDoesNotExist()
            startNewProjectUseCaseWillSucceed()
        }
        whenUseCaseIsExecuted()
        then {
            createdFile as ProjectFile
            createdFile.projectId.mustEqual(createdProjectId)
            createdFile.projectName.mustEqual(requestFileName)
            createdFile.location.mustEqual("$requestDirectory\\$requestFileName.stry")
        }
    }

    @Test
    fun `Start new project success should open created project file`() {
        given {
            directoryExists()
            fileDoesNotExist()
            startNewProjectUseCaseWillSucceed()
        }
        whenUseCaseIsExecuted()
        then {
            output as OpenProject.ResponseModel
            output.projectId.mustEqual(createdProjectId?.uuid) { "Output project id is incorrect" }
            output.projectName.mustEqual(requestFileName) { "Output project name is incorrect" }
            output.projectLocation.mustEqual("$requestDirectory\\$requestFileName.stry") { "Output file location is incorrect" }
        }
    }

    private lateinit var setup: StartNewLocalProjectSetup

    @BeforeEach
    fun cleanSetup() {
        setup = StartNewLocalProjectSetup()
    }

    private var assertions: StartNewLocalProjectAssertions? = null

    @BeforeEach
    fun cleanOutput() {
        assertions = null
    }

    private fun given(setup: StartNewLocalProjectSetup.() -> Unit) {
        this.setup.setup()
    }

    private fun whenUseCaseIsExecuted() {
        val baseUseCase = object : StartNewProject {
            var called = false
                private set
            var createdProject: Project? = null

            override suspend fun invoke(name: String, output: StartNewProject.OutputPort) {
                called = true
                when (val result = setup.baseUseCaseResult) {
                    is Throwable -> output.receiveStartNewProjectFailure(result)
                    is StartNewProject.ResponseModel -> {
                        createdProject = Project(Project.Id(result.projectId), name)
                        output.receiveStartNewProjectResponse(result)
                    }
                    else -> {
                        createdProject = Project(Project.Id(UUID.randomUUID()), name)
                        output.receiveStartNewProjectResponse(
                            StartNewProject.ResponseModel(
                                createdProject!!.id.uuid,
                                name
                            )
                        )
                    }
                }
            }
        }
        val repo = object : FileRepository {
            var createdFile: Any? = null
            override suspend fun doesDirectoryExist(directory: String): Boolean = setup.directoryExists
            override suspend fun doesFileExist(filePath: String): Boolean =
                setup.fileExists

            override suspend fun createFile(projectFile: ProjectFile) {
                createdFile = projectFile
            }
        }
        val openProject = object : OpenProject {
            override suspend fun invoke(location: String, outputPort: OpenProject.OutputPort) {
                val projectFile = repo.createdFile as ProjectFile
                outputPort.receiveOpenProjectResponse(
                    OpenProject.ResponseModel(
                        projectFile.projectId.uuid,
                        projectFile.projectName,
                        location,
                        false
                    )
                )
            }

            override suspend fun forceOpenProject(location: String, outputPort: OpenProject.OutputPort) {}
            override suspend fun replaceOpenProject(location: String, outputPort: OpenProject.OutputPort) {}
        }
        val output = object : StartNewLocalProject.OutputPort {
            var result: Any? = null
            override fun receiveStartNewLocalProjectFailure(exception: LocalProjectException) {
                result = exception
            }

            override fun receiveOpenProjectFailure(failure: ProjectException) {
                result = failure
            }

            override suspend fun receiveOpenProjectResponse(response: OpenProject.ResponseModel) {
                result = response
            }

            override suspend fun receiveCloseProjectResponse(response: CloseProject.ResponseModel) {
                result = response
            }

            override fun receiveCloseProjectFailure(failure: Exception) {}
        }
        val useCase: StartNewLocalProject = StartNewLocalProjectUseCase(repo, baseUseCase, openProject)
        runBlocking {
            useCase.invoke(StartNewLocalProject.RequestModel(setup.requestDirectory, setup.requestFileName), output)
        }
        assertions = StartNewLocalProjectAssertions(
            setup.requestDirectory,
            setup.requestFileName,
            output.result,
            repo.createdFile,
            baseUseCase.createdProject?.id
        )
    }

    private fun then(assertions: StartNewLocalProjectAssertions.() -> Unit) {
        this.assertions!!.assertions()
    }

    class StartNewLocalProjectSetup {

        val requestDirectory = "C:\\Some\\local\\directory"
        val requestFileName = "My epic tale"

        var directoryExists: Boolean = true
            private set
        var fileExists: Boolean = false
            private set
        var baseUseCaseResult: Any? = null
            private set

        fun directoryDoesNotExist() {
            directoryExists = false
        }

        fun directoryExists() {
            directoryExists = true
        }

        fun fileWithNameExists() {
            fileExists = true
        }

        fun fileDoesNotExist() {
            fileExists = false
        }

        fun startNewProjectUseCaseWillThrowError(errorToThrow: Throwable) {
            baseUseCaseResult = errorToThrow
        }

        fun startNewProjectUseCaseWillSucceed() {
            baseUseCaseResult = StartNewProject.ResponseModel(UUID.randomUUID(), requestFileName)
        }
    }

    class StartNewLocalProjectAssertions(
        val requestDirectory: String,
        val requestFileName: String,
        val output: Any?,
        val createdFile: Any?,
        val createdProjectId: Project.Id?
    ) {

        fun Any?.mustEqual(expected: Any?, message: () -> String = { "" }) = assertEquals(expected, this) { message() }

    }

}
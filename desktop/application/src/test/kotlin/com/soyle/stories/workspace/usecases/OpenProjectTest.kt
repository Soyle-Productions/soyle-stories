/**
 * Created by Brendan
 * Date: 2/29/2020
 * Time: 10:26 PM
 */
package com.soyle.stories.workspace.usecases

import com.soyle.stories.entities.Project
import com.soyle.stories.workspace.ProjectDoesNotExistAtLocation
import com.soyle.stories.workspace.ProjectException
import com.soyle.stories.workspace.UnexpectedProjectAlreadyOpenAtLocation
import com.soyle.stories.workspace.entities.Workspace
import com.soyle.stories.workspace.repositories.ProjectRepository
import com.soyle.stories.workspace.repositories.WorkspaceRepository
import com.soyle.stories.workspace.usecases.closeProject.CloseProject
import com.soyle.stories.workspace.usecases.openProject.OpenProject
import com.soyle.stories.workspace.usecases.openProject.OpenProjectUseCase
import com.soyle.stories.workspace.valueobjects.ProjectFile
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

class OpenProjectTest {

    @Test
    fun `project does not exist`() {
        given {
            projectDoesNotExist()
        }
        `when` {
            openProject()
        }
        then {
            workspaceMustNotHaveBeenUpdated()
            val output = output.mustBe<ProjectDoesNotExistAtLocation> { "" }
            output.location.mustEqual(location) { "" }
        }
    }

    @Test
    fun `workspace has open project at requested location`() {
        given {
            projectExists()
            workspaceExists()
            workspaceHasProjectWithRequestedLocation()
        }
        `when` {
            openProject()
        }
        then {
            workspaceMustNotHaveBeenUpdated()
            val output = output.mustBe<UnexpectedProjectAlreadyOpenAtLocation> { "Output is of unexpected type" }
            output.location.mustEqual(location) { "" }
        }
    }

    @Test
    fun `open first project`() {
        given {
            projectExists()
            workspaceDoesNotExist()
        }
        `when` {
            openProject()
        }
        then {
            useCaseShouldHaveSucceeded()
        }
    }

    @Test
    fun `open second project`() {
        given {
            projectExists()
            workspaceExists()
            workspaceContainsOneProject()
        }
        `when` {
            openProject()
        }
        then {
            workspaceMustNotHaveBeenUpdated()
            projectMustBeInOutput()
            val output = output.mustBe<OpenProject.ResponseModel>() { "" }
            output.requiresConfirmation.mustEqual(true) { "" }
        }
    }

    @Test
    fun `open third project`() {
        given {
            projectExists()
            workspaceExists()
            workspaceContainsTwoProjects()
        }
        `when` {
            openProject()
        }
        then {
            useCaseShouldHaveSucceeded()

            firstProjectMustBeInWorkspace()
            secondProjectMustBeInWorkspace()
        }
    }

    @Test
    fun `force second project`() {
        given {
            projectExists()
            workspaceExists()
            workspaceContainsOneProject()
        }
        `when` {
            forceOpenProject()
        }
        then {
            useCaseShouldHaveSucceeded()

            firstProjectMustBeInWorkspace()
        }
    }

    @Test
    fun `replace first project with new project`() {
        given {
            projectExists()
            workspaceExists()
            workspaceContainsOneProject()
        }
        `when` {
            replaceOpenProject()
        }
        then {
            useCaseShouldHaveSucceeded()
            firstProjectMustNotBeInWorkspace()

            closeProjectOutput.mustBe<CloseProject.ResponseModel> { "Close project output is of incorrect type" }
        }
    }

    private lateinit var setup: OpenProjectTestSetup
    private fun given(setup: OpenProjectTestSetup.() -> Unit) {
        this.setup = OpenProjectTestSetup()
        this.setup.setup()
    }

    private class OpenProjectTestSetup {
        val workerId = UUID.randomUUID().toString()
        val projectFile = projectFile()
        val projectUUID = projectFile.projectId.uuid
        val projectName = projectFile.projectName
        val location = projectFile.location

        var projectExists: Boolean = false
            private set
        var workspace: Workspace? = null
            private set

        private fun projectFile() = ProjectFile(
            Project.Id(UUID.randomUUID()),
            "Project - ${UUID.randomUUID()}",
            "other.location.${UUID.randomUUID()}"
        )

        fun projectDoesNotExist() {
            projectExists = false
        }

        fun projectExists() {
            projectExists = true
        }

        fun workspaceExists() {
            workspace = Workspace(workerId, listOf())
        }

        fun workspaceDoesNotExist() {
            workspace = null
        }

        fun workspaceContainsOneProject() {
            workspace = Workspace(workerId, listOf(projectFile()))
        }

        fun workspaceContainsTwoProjects() {
            workspace = Workspace(workerId, listOf(projectFile(), projectFile()))
        }

        fun workspaceHasProjectWithRequestedLocation() {
            workspace = Workspace(
                workerId,
                (workspace?.openProjects ?: listOf()) + ProjectFile(Project.Id(UUID.randomUUID()), "Project - ${UUID.randomUUID()}", location)
            )
        }
    }

    private lateinit var executable: OpenProjectExecution
    private fun `when`(executions: OpenProjectExecution.() -> Unit) {
        executable = OpenProjectExecution(
            setup.workerId,
            setup.location,
            setup.workspace,
            setup.projectFile.takeIf { setup.projectExists }
        )
        executable.executions()
    }

    private class OpenProjectExecution(
        workerId: String,
        val location: String,
        val workspace: Workspace?,
        val project: ProjectFile?
    ) {

        var createdWorkspace: Workspace? = null
            private set
        var updatedWorkspace: Workspace? = null
            private set
        private val workspaceRepository = object : WorkspaceRepository {
            override suspend fun addNewWorkspace(workspace: Workspace) {
                createdWorkspace = workspace
            }

            override suspend fun getWorkSpaceForWorker(workerId: String): Workspace? = updatedWorkspace ?: createdWorkspace ?: workspace

            override suspend fun updateWorkspace(workspace: Workspace) {
                updatedWorkspace = workspace
            }
        }
        private val projectRepository = object : ProjectRepository {
            override suspend fun getProjectAtLocation(location: String): Project? =
                project?.let { Project(it.projectId, it.projectName) }
        }

        private val closeProjectUseCase = object : CloseProject {
            override suspend fun invoke(projectId: UUID, outputPort: CloseProject.OutputPort) {
                updatedWorkspace = Workspace(workerId, (updatedWorkspace?.openProjects?.filterNot { it.projectId.uuid == projectId } ?: listOf()))
                outputPort.receiveCloseProjectResponse(CloseProject.ResponseModel(projectId))
            }
        }

        private val useCase: OpenProject = OpenProjectUseCase(
            workerId,
            workspaceRepository,
            projectRepository,
            closeProjectUseCase
        )

        var result: Any? = null
            private set
        var closeProjectResult: Any? = null
            private set
        private val outputPort: OpenProject.OutputPort = object : OpenProject.OutputPort {
            override fun receiveOpenProjectFailure(failure: ProjectException) {
                result = failure
            }

            override suspend fun receiveCloseProjectResponse(response: CloseProject.ResponseModel) {
                closeProjectResult = response
            }

            override suspend fun receiveOpenProjectResponse(response: OpenProject.ResponseModel) {
                result = response
            }
            override fun receiveCloseProjectFailure(failure: Exception) {
                closeProjectResult = failure
            }
        }

        fun openProject() {
            runBlocking {
                useCase.invoke(location, outputPort)
            }
        }

        fun forceOpenProject() {
            runBlocking {
                useCase.forceOpenProject(location, outputPort)
            }
        }

        fun replaceOpenProject() {
            runBlocking {
                useCase.replaceOpenProject(location, outputPort)
            }
        }

    }

    private fun then(assert: OpenProjectAssertions.() -> Unit) {
        OpenProjectAssertions(
            executable.result,
            executable.closeProjectResult,
            executable.location,
            executable.project,
            executable.createdWorkspace,
            executable.updatedWorkspace,
            executable.workspace?.openProjects ?: listOf()
        ).assert()
    }

    class OpenProjectAssertions(
        val output: Any?,
        val closeProjectOutput: Any?,
        val location: String,
        val openedProject: ProjectFile?,
        private val createdWorkspace: Workspace?,
        private val updatedWorkspace: Workspace?,
        private val originalProjectList: List<ProjectFile>
    ) {

        inline fun <reified T> Any?.mustBe(message: () -> String = { "" }): T {
            assert(this is T) { "${message()}:\n $this is not ${T::class.simpleName}" }
            return this as T
        }

        fun Any?.mustEqual(expected: Any?, message: () -> String = { "" }) {
            assertEquals(expected, this) { message() }
        }

        fun workspaceMustNotHaveBeenUpdated() {
            assertNull(updatedWorkspace) { "Workspace should not have been updated" }
        }

        fun workspaceMustHaveBeenCreated() {
            assertNotNull(createdWorkspace) { "Workspace should have been created" }
        }

        fun projectMustBeInWorkspace() {
            if (openedProject == null) error("Opened project is null")
            val workspace = updatedWorkspace ?: createdWorkspace ?: error("Workspace was never updated or created")
            val openProject = workspace.openProjects.find { it.projectId == openedProject.projectId }
                ?: error("Workspace does not have project with opened project id")
            openProject.projectName.mustEqual(openedProject.projectName) { "Project names are not equal" }
            openProject.location.mustEqual(openedProject.location) { "Project locations are not equal" }
        }

        fun projectMustBeInOutput() {
            if (openedProject == null) error("Opened project is null")
            val output = output.mustBe<OpenProject.ResponseModel>()
            output.projectId.mustEqual(openedProject.projectId.uuid) { "Incorrect project id in output" }
            output.projectName.mustEqual(openedProject.projectName) { "Incorrect project name in output" }
            output.projectLocation.mustEqual(openedProject.location) { "Incorrect project location in output" }
        }

        fun workspaceMustHaveBeenUpdated() {
            assertNotNull(updatedWorkspace) { "Workspace should have been updated" }
        }

        fun firstProjectMustBeInWorkspace() {
            assert(
                updatedWorkspace!!.openProjects.contains(originalProjectList.first())
            ) { "First project not in updated workspace" }
        }

        fun useCaseShouldHaveSucceeded() {
            workspaceMustHaveBeenUpdated()
            projectMustBeInWorkspace()
            projectMustBeInOutput()
        }

        fun firstProjectMustNotBeInWorkspace() {
            assert(
                updatedWorkspace?.openProjects?.contains(originalProjectList.first()) != true
            ) { "First project still in updated workspace" }
        }

        fun secondProjectMustBeInWorkspace() {
            assert(
                updatedWorkspace!!.openProjects.contains(originalProjectList.component2())
            ) { "Second project not in updated workspace" }
        }
    }
}
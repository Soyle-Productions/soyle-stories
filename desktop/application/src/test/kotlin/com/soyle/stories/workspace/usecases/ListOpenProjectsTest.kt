/**
 * Created by Brendan
 * Date: 2/29/2020
 * Time: 11:58 AM
 */
package com.soyle.stories.workspace.usecases

import arrow.core.Either
import arrow.core.right
import com.soyle.stories.entities.Project
import com.soyle.stories.translators.asProject
import com.soyle.stories.workspace.ExpectedProjectDoesNotExistAtLocation
import com.soyle.stories.workspace.UnexpectedProjectExistsAtLocation
import com.soyle.stories.workspace.entities.Workspace
import com.soyle.stories.workspace.repositories.ProjectRepository
import com.soyle.stories.workspace.repositories.WorkspaceRepository
import com.soyle.stories.workspace.usecases.listOpenProjects.ListOpenProjects
import com.soyle.stories.workspace.usecases.listOpenProjects.ListOpenProjectsUseCase
import com.soyle.stories.workspace.valueobjects.ProjectFile
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

class ListOpenProjectsTest {

    val workerId = UUID.randomUUID().toString()

    private fun given(workspaces: List<Workspace>, projects: List<ProjectFile>): () -> Either<*, *> {
        val repo = object : WorkspaceRepository, ProjectRepository {
            override suspend fun addNewWorkspace(workspace: Workspace) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            override suspend fun getWorkSpaceForWorker(workerId: String): Workspace? = workspaces.find { it.workerId == workerId }
            override suspend fun updateWorkspace(workspace: Workspace) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override suspend fun getProjectAtLocation(location: String): Project? = projects.find { it.location == location }?.let {
                it.asProject()
            }
        }
        val useCase: ListOpenProjects = ListOpenProjectsUseCase(workerId, repo, repo)
        val output = object : ListOpenProjects.OutputPort {
            var result: Either<*, *>? = null
            override fun receiveListOpenProjectsResponse(response: ListOpenProjects.ResponseModel) {
                result = response.right()
            }
        }
        return {
            runBlocking {
                useCase.invoke(output)
            }
            output.result ?: error("no output received")
        }
    }

    @Nested
    inner class GivenWorkspaceDoesNotExist {

        val useCase = given(emptyList(), emptyList())

        @Test
        fun `output should be empty`() {
            val (result) = useCase.invoke() as Either.Right
            result as ListOpenProjects.ResponseModel
            assert(result.openProjects.isEmpty())
        }

    }

    val projectUUID = UUID.randomUUID()
    val projectName = "Project"
    val projectLocation = "documents"

    @Nested
    inner class GivenProjectFileDoesNotExistAtLocation {

        val useCase = given(
            workspaces = listOf(
                Workspace(workerId, listOf(ProjectFile(Project.Id(projectUUID), projectName, projectLocation)))
            ),
            projects = emptyList()
        )

        @Test
        fun `should output project does not exist error`() {
            val (result) = useCase.invoke() as Either.Right
            result as ListOpenProjects.ResponseModel
            val failure = result.failedProjects.single()
            failure as ExpectedProjectDoesNotExistAtLocation
            assertEquals(projectUUID, failure.expectedProjectId)
            assertEquals(projectName, failure.expectedProjectName)
            assertEquals(projectLocation, failure.location)
        }

    }

    @Nested
    inner class GivenDifferentProjectFileExistsAtLocation {

        val differentProjectUUID = UUID.randomUUID()
        val differentProjectName = "Other Project"

        val useCase = given(
            workspaces = listOf(
                Workspace(workerId, listOf(ProjectFile(Project.Id(projectUUID), projectName, projectLocation)))
            ),
            projects = listOf(
                ProjectFile(Project.Id(differentProjectUUID), differentProjectName, projectLocation)
            )
        )

        @Test
        fun `unexpected project at location error`() {
            val (result) = useCase.invoke() as Either.Right
            result as ListOpenProjects.ResponseModel
            val failure = result.failedProjects.single()
            failure as UnexpectedProjectExistsAtLocation
            assertEquals(projectUUID, failure.expectedProjectId)
            assertEquals(projectName, failure.expectedProjectName)
            assertEquals(projectLocation, failure.location)
            assertEquals(differentProjectUUID, failure.foundProjectId)
            assertEquals(differentProjectName, failure.foundProjectName)
        }

    }

    @Nested
    inner class GivenProjectExistsAndMatchesExpectations {

        val useCase = given(
            workspaces = listOf(
                Workspace(workerId, listOf(ProjectFile(Project.Id(projectUUID), projectName, projectLocation)))
            ),
            projects = listOf(
                ProjectFile(Project.Id(projectUUID), projectName, projectLocation)
            )
        )

        @Test
        fun `should output project item`() {
            val (result) = useCase.invoke() as Either.Right
            result as ListOpenProjects.ResponseModel
            val projectItem = result.openProjects.single()
            assertEquals(projectUUID, projectItem.projectId)
            assertEquals(projectName, projectItem.projectName)
            assertEquals(projectLocation, projectItem.projectLocation)
        }

    }

}
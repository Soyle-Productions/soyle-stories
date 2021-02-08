/**
 * Created by Brendan
 * Date: 3/1/2020
 * Time: 10:57 AM
 */
package com.soyle.stories.workspace.usecases

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.soyle.stories.domain.project.Project
import com.soyle.stories.workspace.ProjectNotOpen
import com.soyle.stories.workspace.entities.Workspace
import com.soyle.stories.workspace.repositories.WorkspaceRepository
import com.soyle.stories.workspace.usecases.closeProject.CloseProject
import com.soyle.stories.workspace.usecases.requestCloseProject.RequestCloseProject
import com.soyle.stories.workspace.usecases.requestCloseProject.RequestCloseProjectUseCase
import com.soyle.stories.workspace.valueobjects.ProjectFile
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

class RequestCloseProjectTest {

    val workerId = UUID.randomUUID().toString()

    private fun given(
        workspaces: List<Workspace>,
        closeProject: () -> Unit = {}
    ): (UUID) -> Either<*, *> {
        val repo = object : WorkspaceRepository {
            override suspend fun getWorkSpaceForWorker(workerId: String): Workspace? = workspaces.find { it.workerId == workerId }
            override suspend fun updateWorkspace(workspace: Workspace) = Unit
            override suspend fun addNewWorkspace(workspace: Workspace) = Unit
        }
        val output = object : RequestCloseProject.OutputPort {
            var result: Either<*, *>? = null
            override fun receiveCloseProjectFailure(failure: Exception) {
                result = failure.left()
            }

            override suspend fun receiveCloseProjectResponse(response: CloseProject.ResponseModel) { }
            override fun receiveConfirmCloseProjectRequest(request: RequestCloseProject.ResponseModel) {
                result = request.right()
            }
        }
        val closeProjectUseCase = object : CloseProject {
            override suspend fun invoke(projectId: UUID, outputPort: CloseProject.OutputPort) {
                closeProject.invoke()
                output.result = Unit.right()
            }
        }
        val useCase: RequestCloseProject = RequestCloseProjectUseCase(workerId, closeProjectUseCase, repo)
        return {
            runBlocking {
                useCase.invoke(it, output)
            }
            output.result!!
        }
    }

    val projectUUID = UUID.randomUUID()
    val projectName = "Project"

    @Nested
    inner class `When workspace does not exist` {

        val useCase = given(emptyList())

        @Test
        fun `should output project not open error`() {
            val (error) = useCase.invoke(projectUUID) as Either.Left
            error as ProjectNotOpen
            assertEquals(projectUUID, error.projectId)
        }

    }

    @Nested
    inner class `Given workspace exists` {

        @Nested
        inner class `And only contains one project` {

            @Nested
            inner class `And project is not requested project` {

                val useCase = given(listOf(
                    Workspace(workerId, listOf(ProjectFile(Project.Id(UUID.randomUUID()), "", "")))
                ))

                @Test
                fun `should output project not open error`() {
                    val (error) = useCase.invoke(projectUUID) as Either.Left
                    error as ProjectNotOpen
                    assertEquals(projectUUID, error.projectId)
                }

            }

            @Nested
            inner class `And project is requested project` {

                val useCase = given(listOf(
                    Workspace(workerId, listOf(ProjectFile(Project.Id(projectUUID), projectName, "")))
                ))

                @Test
                fun `should request close confirmation`() {
                    val (result) = useCase.invoke(projectUUID) as Either.Right
                    result as RequestCloseProject.ResponseModel
                    assertEquals(projectUUID, result.projectId)
                    assertEquals(projectName, result.projectName)
                }

            }

        }

        @Nested
        inner class `And contains more or less than one project` {

            val workspaceLists = listOf(
                listOf(Workspace(workerId, listOf())),
                listOf(Workspace(workerId, List(5) { ProjectFile(Project.Id(UUID.randomUUID()), "", "") }))
            )

            @Test
            fun `should call close project use case`() {
                workspaceLists.forEach {
                    var closeProjectCalled = false
                    given(it, closeProject = {
                        closeProjectCalled = true
                    }).invoke(projectUUID)
                    assert(closeProjectCalled) { "Close project was not called when workspace has ${it.size} project(S)" }
                }
            }

        }

    }

}
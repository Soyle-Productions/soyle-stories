/**
 * Created by Brendan
 * Date: 3/1/2020
 * Time: 10:17 AM
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
import com.soyle.stories.workspace.usecases.closeProject.CloseProjectUseCase
import com.soyle.stories.workspace.valueobjects.ProjectFile
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

class CloseProjectTest {

    val workerId = UUID.randomUUID().toString()

    private fun given(
        workspaces: List<Workspace>,
        updateWorkspace: (Workspace) -> Unit = {}
    ): (UUID) -> Either<*, *> {
        val repo = object : WorkspaceRepository {
            override suspend fun getWorkSpaceForWorker(workerId: String): Workspace? = workspaces.find { it.workerId == workerId }
            override suspend fun updateWorkspace(workspace: Workspace) = updateWorkspace.invoke(workspace)
            override suspend fun addNewWorkspace(workspace: Workspace) = Unit
        }
        val useCase: CloseProject = CloseProjectUseCase(workerId, repo)
        val output = object : CloseProject.OutputPort {
            var result: Either<*, *>? = null
            override fun receiveCloseProjectFailure(failure: Exception) {
                result = failure.left()
            }

            override suspend fun receiveCloseProjectResponse(response: CloseProject.ResponseModel) {
                result = response.right()
            }
        }
        return {
            runBlocking {
                useCase.invoke(it, output)
            }
            output.result!!
        }
    }

    val projectUUID = UUID.randomUUID()

    @Nested
    inner class `When workspace does not exist OR does not contain project` {

        val useCases = listOf(
            given(emptyList()),
            given(listOf(Workspace(workerId, emptyList())))
        )

        @Test
        fun `should output project not open error`() {
            useCases.map {
                it.invoke(projectUUID)
            }.forEach {
                val (error) = it as Either.Left
                error as ProjectNotOpen
                assertEquals(projectUUID, error.projectId)
            }
        }

    }

    @Nested
    inner class `Given Workspace exists and contains a project with requested id` {

        val useCase = given(listOf(
            Workspace(workerId, listOf(ProjectFile(Project.Id(projectUUID), "", "")))
        ))

        @Test
        fun `should output closed project id`() {
            val (result) = useCase.invoke(projectUUID) as Either.Right
            result as CloseProject.ResponseModel
            assertEquals(projectUUID, result.projectId)
        }

        @Test
        fun `project should be removed from workspace`() {
            var updatedWorkspace: Workspace? = null
            given(listOf(
                Workspace(workerId, listOf(ProjectFile(Project.Id(projectUUID), "", "")))
            ), updateWorkspace = {
                updatedWorkspace = it
            }).invoke(projectUUID)
            assert(updatedWorkspace!!.openProjects.isEmpty())
        }

    }

}
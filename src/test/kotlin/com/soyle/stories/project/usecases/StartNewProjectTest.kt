/**
 * Created by Brendan
 * Date: 2/29/2020
 * Time: 10:40 AM
 */
package com.soyle.stories.project.usecases

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.soyle.stories.entities.Project
import com.soyle.stories.project.NameCannotBeBlank
import com.soyle.stories.project.repositories.ProjectRepository
import com.soyle.stories.project.usecases.startNewProject.StartNewProject
import com.soyle.stories.project.usecases.startNewProject.StartNewProjectUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.util.*

class StartNewProjectTest {

	private fun given(addNewProject: (Project) -> Unit = {}): (String) -> Either<*, *> {
		val repo = object : ProjectRepository {
			override suspend fun addNewProject(project: Project) = addNewProject.invoke(project)
		}
		val useCase: StartNewProject = StartNewProjectUseCase(repo)
		val output = object : StartNewProject.OutputPort {
			var result: Either<*, *>? = null
			override fun receiveStartNewProjectFailure(failure: Throwable) {
				result = failure.left()
			}

			override fun receiveStartNewProjectResponse(response: StartNewProject.ResponseModel) {
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

	private val authorUUID = UUID.randomUUID()
	private val projectName = "My Awesome project"

	@Test
	fun `blank name should produce failure`() {
		val (error) = given().invoke("  ") as Either.Left
		error as NameCannotBeBlank
	}

	@Test
	fun `valid name should produce project`() {
		val (result) = given().invoke(projectName) as Either.Right
		result as StartNewProject.ResponseModel
		assertEquals(projectName, result.projectName)
		result.projectId
	}

	@Test
	fun `project should be persisted`() {
		var addedProject: Project? = null
		given(addNewProject = {
			addedProject = it
		}).invoke(projectName)
		assertNotNull(addedProject)
	}

}
package com.soyle.stories.usecase.project

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.soyle.stories.domain.nonBlankStr
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.project.startNewProject.StartNewProject
import com.soyle.stories.usecase.project.startNewProject.StartNewProjectUseCase
import com.soyle.stories.usecase.repositories.ProjectRepositoryDouble
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.util.*

class StartNewProjectTest {

	private fun given(addNewProject: (Project) -> Unit = {}): (NonBlankString) -> Either<*, *> {
		val repo = ProjectRepositoryDouble(addNewProject)
		val useCase: StartNewProject = StartNewProjectUseCase(repo)
		val output = object : StartNewProject.OutputPort {
			var result: Either<*, *>? = null
			override fun receiveStartNewProjectFailure(failure: Throwable) {
				result = failure.left()
			}

			override suspend fun receiveStartNewProjectResponse(response: StartNewProject.ResponseModel) {
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
	private val projectName = nonBlankStr("My Awesome project")

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
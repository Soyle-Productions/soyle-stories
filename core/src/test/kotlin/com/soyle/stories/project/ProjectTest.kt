package com.soyle.stories.project

import arrow.core.Either
import arrow.core.flatMap
import com.soyle.stories.entities.Project
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * Created by Brendan
 * Date: 2/10/2020
 * Time: 2:17 PM
 */
class ProjectTest {

	val projectName = "Test Name"

	@Test
	fun `project should have provided name`() {
		val (project) = Project.startNew(projectName) as Either.Right
		assertEquals(projectName, project.name)
	}

	@Test
	fun canRenameProject() {
		val (project) = Project.startNew("Initial Name")
			.flatMap { it.rename(projectName) } as Either.Right
		assertEquals(projectName, project.name)
	}

	@Nested
	inner class GivenInvalidName {

		@Test
		fun shouldReturnNameCannotBeBlankError() {
			val (error) = Project.startNew("     \n  \r") as Either.Left
			assert(error is NameCannotBeBlank)
		}

		@Test
		fun renameShouldReturnNameCannotBeBlankError() {
			val (error) = Project.startNew(projectName)
				.flatMap { it.rename("   ") } as Either.Left
			assert(error is NameCannotBeBlank)
		}

	}

}
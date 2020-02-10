package com.soyle.studio.project

import arrow.core.Either
import arrow.core.flatMap
import com.soyle.studio.project.events.ProjectRenamed
import com.soyle.studio.project.events.ProjectStarted
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.net.URISyntaxException

/**
 * Created by Brendan
 * Date: 2/10/2020
 * Time: 2:17 PM
 */
class ProjectTest {

	val projectName = "Test Name"
	val projectURI = ""

	@Test
	fun canStartNewProject() {
		val (project) = Project.startNew(projectName, projectURI) as Either.Right
		assert(project.events.contains(ProjectStarted(project.id)))
		assertEquals(projectName, project.name)
	}

	@Test
	fun canRenameProject() {
		val (project) = Project.startNew("Initial Name", projectURI)
			.flatMap { it.rename(projectName) } as Either.Right
		assert(project.events.contains(ProjectRenamed(project.id, projectName)))
		assertEquals(projectName, project.name)
	}

	@Nested
	inner class GivenInvalidURI {

		@Test
		fun shouldReturnURISyntaxException() {
			val (error) = Project.startNew(projectName, "blah blah blah") as Either.Left
			assert(error is URISyntaxException)
		}

	}

	@Nested
	inner class GivenInvalidName {

		@Test
		fun shouldReturnNameCannotBeBlankError() {
			val (error) = Project.startNew("     \n  \r", projectURI) as Either.Left
			assert(error is NameCannotBeBlank)
		}

		@Test
		fun renameShouldReturnNameCannotBeBlankError() {
			val (error) = Project.startNew(projectName, projectURI)
				.flatMap { it.rename("   ") } as Either.Left
			assert(error is NameCannotBeBlank)
		}

	}

}
package com.soyle.stories.domain.project

import com.soyle.stories.domain.nonBlankStr
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ProjectTest {

	private val projectName = nonBlankStr("Test Name")

	@Test
	fun `project should have provided name`() {
		val project = Project.startNew(projectName)
		assertEquals(projectName, project.name)
	}

	@Test
	fun canRenameProject() {
		val project = Project.startNew(nonBlankStr("Initial Name"))
			.rename(projectName)
		assertEquals(projectName, project.name)
	}

}
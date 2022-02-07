package com.soyle.stories.domain.project

import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.nonBlankStr
import com.soyle.stories.domain.project.changes.ProjectRenamed
import com.soyle.stories.domain.project.changes.ProjectStarted
import com.soyle.stories.domain.project.exceptions.ProjectAlreadyNamed
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ProjectTest {

	@Test
	fun `can create project`() {
		val update = Project.startNew(nonBlankStr("Test Name"))

		update as ProjectUpdate.Successful
		update.change.mustEqual(ProjectStarted(update.project.id, "Test Name"))
	}

	@Nested
	inner class `Rename Project` {

		@Test
		fun `can rename project`() {
			val (project) = Project.startNew(nonBlankStr("Test Name"))

			val update = project.withName(nonBlankStr("New Name"))

			update as ProjectUpdate.Successful
			update.change.mustEqual(ProjectRenamed(project.id, "Test Name", "New Name"))
		}

		@Test
		fun `cannot rename project to same name`() {
			val (project) = Project.startNew(nonBlankStr("Test Name"))

			val update = project.withName(nonBlankStr("Test Name"))

			update as ProjectUpdate.UnSuccessful
			update.reason.mustEqual(ProjectAlreadyNamed(project.id, "Test Name"))
		}

		@Test
		fun `cannot rename project twice`() {
			val (project) = Project.startNew(nonBlankStr("Test Name"))
				.project.withName(nonBlankStr("New Name"))

			val update = project.withName(nonBlankStr("New Name"))

			update as ProjectUpdate.UnSuccessful
			update.reason.mustEqual(ProjectAlreadyNamed(project.id, "New Name"))
		}

	}

}
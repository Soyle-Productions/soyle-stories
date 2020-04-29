package com.soyle.stories.soylestories

import com.soyle.stories.project.projectList.ProjectFileViewModel
import javafx.collections.FXCollections
import javafx.stage.Stage
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testfx.api.FxToolkit
import org.testfx.framework.junit5.ApplicationTest
import tornadofx.FX
import tornadofx.find
import java.util.*

class ApplicationScopeUnitTest : ApplicationTest() {

	private lateinit var scope: ApplicationScope
	private lateinit var model: ApplicationModel

	@BeforeEach
	fun setupFX() {
		scope = ApplicationScope()
		val primaryStage = FxToolkit.registerPrimaryStage()
		FX.setPrimaryStage(scope, primaryStage)
		model = find(scope = scope)
		model.openProjects.unbind()
	}

	@Test
	fun `no open projects`() {
		interact {
			model.openProjects.set(FXCollections.observableArrayList())
		}
		assertThat(scope.projectScopes).isEmpty()
	}

	@Test
	fun `one open project`() {
		interact {
			model.openProjects.set(FXCollections.observableArrayList(ProjectFileViewModel(UUID.randomUUID(), "Project Name", "Some place somewhere")))
		}
		assertThat(scope.projectScopes).isNotEmpty()
	}

	@Test
	fun `project closed`() {
		interact {
			model.openProjects.set(FXCollections.observableArrayList(ProjectFileViewModel(UUID.randomUUID(), "Project Name", "Some place somewhere")))
			model.openProjects.set(FXCollections.observableArrayList())
		}
		assertThat(scope.projectScopes).isEmpty()
	}

}
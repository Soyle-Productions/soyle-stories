package com.soyle.stories.soylestories

import com.soyle.stories.project.ProjectLoadingDialog
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.WorkBench
import com.soyle.stories.project.projectList.ProjectFileViewModel
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleSetProperty
import javafx.collections.FXCollections
import javafx.stage.Stage
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testfx.api.FxToolkit
import org.testfx.framework.junit5.ApplicationTest
import tornadofx.FX
import tornadofx.find
import tornadofx.observableListOf
import java.util.*

class ApplicationViewUnitTest : ApplicationTest() {

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
	fun `create workbench`() {
		val view = find<SoyleStoriesView>(scope = scope)
		interact {
			model.openProjects.set(observableListOf(ProjectFileViewModel(UUID.randomUUID(), "Project Name", "Some place somewhere")))
		}
		assertThat(view.projectViews.single().scope).isEqualTo(scope.projectScopes.first())
	}

	@Test
	fun `close workbench`() {
		val view = find<SoyleStoriesView>(scope = scope)
		var workBench: WorkBench? = null
		interact {
			model.openProjects.set(observableListOf(ProjectFileViewModel(UUID.randomUUID(), "Project Name", "Some place somewhere")))
			workBench = FX.getComponents(scope.projectScopes.first()).values.filterIsInstance<WorkBench>().single()
			model.openProjects.set(observableListOf())
		}
		assertThat(view.projectViews).doesNotContain(workBench)
	}

}
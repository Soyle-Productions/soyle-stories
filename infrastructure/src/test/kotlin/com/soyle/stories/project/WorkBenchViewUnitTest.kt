package com.soyle.stories.project

import com.soyle.stories.di.DI
import com.soyle.stories.layout.tools.FixedTool
import com.soyle.stories.project.layout.Dialog
import com.soyle.stories.project.layout.LayoutViewListener
import com.soyle.stories.project.projectList.ProjectFileViewModel
import com.soyle.stories.project.projectList.ProjectListViewListener
import com.soyle.stories.soylestories.ApplicationScope
import com.soyle.stories.testutils.findComponentsInScope
import javafx.event.EventTarget
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.testfx.api.FxToolkit
import org.testfx.framework.junit5.ApplicationTest
import tornadofx.FX
import tornadofx.find
import java.net.URI
import java.util.*
import kotlin.reflect.KClass

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WorkBenchViewUnitTest : ApplicationTest() {

	@BeforeAll
	fun registerListeners() {
		DI.registerTypeFactory<LayoutViewListener, ProjectScope> {
			object : LayoutViewListener {
				override fun closeDialog(dialog: KClass<out Dialog>) {
					TODO("Not yet implemented")
				}

				override suspend fun closeTool(toolId: String) {
					TODO("Not yet implemented")
				}

				override fun loadLayoutForProject(projectId: UUID) {
				}

				override fun openDialog(dialog: Dialog) {
					TODO("Not yet implemented")
				}

				override suspend fun toggleToolOpen(tool: FixedTool) {

				}
			}
		}
		DI.registerTypeFactory<ProjectListViewListener, ApplicationScope> {
			object : ProjectListViewListener {
				override suspend fun startApplicationWithParameters(parameters: List<String>) {
					TODO("Not yet implemented")
				}

				override suspend fun requestCloseProject(projectId: UUID) {
					TODO("Not yet implemented")
				}

				override suspend fun closeProject(projectId: UUID) {
					TODO("Not yet implemented")
				}

				override suspend fun ignoreFailure(failingURI: URI) {
					TODO("Not yet implemented")
				}

				override suspend fun startNewProject(directory: String, name: String) {
					TODO("Not yet implemented")
				}

				override suspend fun openProject(location: String) {
					TODO("Not yet implemented")
				}

				override suspend fun replaceCurrentProject(location: String) {
					TODO("Not yet implemented")
				}

				override suspend fun forceOpenProject(location: String) {
					TODO("Not yet implemented")
				}

			}
		}
	}

	val appScope = ApplicationScope()

	@BeforeEach
	fun setupFX() {
		FX.setPrimaryStage(appScope, FxToolkit.registerPrimaryStage())
	}

	@Test
	fun `display dialog while loading`() {
		var dialog: ProjectLoadingDialog? = null
		interact {
			val scope = ProjectScope(appScope, ProjectFileViewModel(UUID.randomUUID(), "", ""))
			find<WorkBench>(scope)
			dialog = findComponentsInScope<ProjectLoadingDialog>(scope).single()
		}
		Assertions.assertThat(dialog!!.currentStage!!.isShowing).isTrue()
	}

	@Test
	fun `close dialog when done loading`() {
		var dialog: ProjectLoadingDialog? = null
		interact {
			val scope = ProjectScope(appScope, ProjectFileViewModel(UUID.randomUUID(), "", ""))
			find<WorkBench>(scope)
			find<WorkBenchModel>(scope).loadingProgress.unbind()
			find<WorkBenchModel>(scope).loadingProgress.set(WorkBenchModel.MAX_LOADING_VALUE)
			dialog = findComponentsInScope<ProjectLoadingDialog>(scope).single()
		}
		Assertions.assertThat(dialog!!.currentStage!!.isShowing).isFalse()
	}

}

class ExpectedNode<N : EventTarget>(val nodeType: KClass<N>, val id: String)
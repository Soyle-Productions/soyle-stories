package com.soyle.stories.project

import com.soyle.stories.di.DI
import com.soyle.stories.layout.tools.FixedTool
import com.soyle.stories.project.layout.Dialog
import com.soyle.stories.project.layout.LayoutViewListener
import com.soyle.stories.project.projectList.ProjectFileViewModel
import com.soyle.stories.project.projectList.ProjectListViewListener
import com.soyle.stories.soylestories.ApplicationScope
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.testfx.api.FxToolkit
import org.testfx.framework.junit5.ApplicationTest
import tornadofx.*
import java.net.URI
import java.util.*
import kotlin.reflect.KClass

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WorkBenchInputUnitTest : ApplicationTest() {

	val calls = mutableSetOf<String>()

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
					calls.add("loadLayoutForProject")
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

	@BeforeEach
	fun clearPastCalls() {
		calls.clear()
	}

	private val appScope = ApplicationScope()

	@BeforeEach
	fun setupFX() {
		FX.setPrimaryStage(appScope, FxToolkit.registerPrimaryStage())
	}

	@Test
	fun `load layout when created`() {
		interact {
			val scope = ProjectScope(appScope, ProjectFileViewModel(UUID.randomUUID(), "", ""))
			registerListeners()
			find<WorkBench>(scope)
		}
		assertThat(calls).contains("loadLayoutForProject")
	}

}
package com.soyle.stories.project

import com.soyle.stories.di.DI
import com.soyle.stories.di.get
import com.soyle.stories.project.layout.Dialog
import com.soyle.stories.project.layout.LayoutViewListener
import com.soyle.stories.project.projectList.ProjectFileViewModel
import com.soyle.stories.project.projectList.ProjectListViewListener
import com.soyle.stories.soylestories.ApplicationScope
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import org.assertj.core.api.Assertions.assertThat
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
class WorkBenchInputUnitTest : ApplicationTest() {

	val calls = mutableSetOf<String>()

	fun registerListeners() {
		DI.registerTypeFactory<WorkBenchViewListener, ProjectScope> {
			object : WorkBenchViewListener {
				override fun createNewCharacter() {
					calls.add("createNewCharacter")
				}

				override fun createNewLocation() {
					calls.add("createNewLocation")
				}

				override fun createNewProject() {
					calls.add("createNewProject")
				}

				override fun createNewScene() {
					calls.add("createNewScene")
				}
			}
		}
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

				override suspend fun toggleToolOpen(toolId: String) {

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

	@Test
	fun `domain concepts can be created through file - new menu`() {
		val expectedOptions = listOf("project", "character", "location", "scene")
		interact {
			val scope = ProjectScope(appScope, ProjectFileViewModel(UUID.randomUUID(), "", ""))
			registerListeners()
			val bar = from(scope.get<WorkBench>().root).lookup(".menu-bar").query<MenuBar>()
			val fileMenu = bar.menus.find { it.id == "file" }!!
			val fileNewMenu = fileMenu.items.find { it.id == "file_new" } as Menu
			val optionItems = fileNewMenu.items.associateBy { it.id }
			expectedOptions.forEach {
				optionItems.getValue("file_new_$it").fire()
				assertThat(calls).contains("createNew${it.capitalize()}")
			}
		}
	}

}
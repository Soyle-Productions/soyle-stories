package com.soyle.stories.project.workbench

import com.soyle.stories.project.WorkBenchController
import com.soyle.stories.project.WorkBenchViewListener
import com.soyle.stories.project.layout.Dialog
import com.soyle.stories.project.layout.LayoutViewListener
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.reflect.KClass

class WorkBenchControllerUnitTest {

	var openedDialog: Dialog? = null

	@Test
	fun `create new project opens dialog`() {
		val viewListener: WorkBenchViewListener = getViewListener()
		viewListener.createNewProject()
		assertEquals(Dialog.CreateProject, openedDialog)
	}

	@Test
	fun `create new character opens dialog`() {
		val viewListener: WorkBenchViewListener = getViewListener()
		viewListener.createNewCharacter()
		assertEquals(Dialog.CreateCharacter, openedDialog)
	}

	@Test
	fun `create new location opens dialog`() {
		val viewListener: WorkBenchViewListener = getViewListener()
		viewListener.createNewLocation()
		assertEquals(Dialog.CreateLocation, openedDialog)
	}

	@Test
	fun `create new scene opens dialog`() {
		val viewListener: WorkBenchViewListener = getViewListener()
		viewListener.createNewScene()
		assertEquals(Dialog.CreateScene, openedDialog)
	}

	private fun getViewListener(): WorkBenchViewListener = WorkBenchController(object : LayoutViewListener {
		override fun loadLayoutForProject(projectId: UUID) {
			TODO("Not yet implemented")
		}

		override suspend fun toggleToolOpen(toolId: String) {
			TODO("Not yet implemented")
		}

		override suspend fun closeTool(toolId: String) {
			TODO("Not yet implemented")
		}

		override fun openDialog(dialog: Dialog) {
			openedDialog = dialog
		}

		override fun closeDialog(dialog: KClass<out Dialog>) {
			TODO("Not yet implemented")
		}
	})
}
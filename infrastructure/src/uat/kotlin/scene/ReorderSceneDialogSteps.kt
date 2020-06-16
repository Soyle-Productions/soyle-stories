package com.soyle.stories.scene

import com.soyle.stories.di.get
import com.soyle.stories.entities.Scene
import com.soyle.stories.project.ProjectSteps
import com.soyle.stories.project.WorkBench
import com.soyle.stories.scene.ReorderSceneDialogSteps.Driver.interact
import com.soyle.stories.scene.reorderSceneDialog.ReorderSceneDialog
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import io.cucumber.java8.En
import javafx.event.ActionEvent
import javafx.scene.control.Button
import javafx.scene.control.ButtonBar
import javafx.scene.control.CheckBox
import javafx.scene.control.DialogPane
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.testfx.framework.junit5.ApplicationTest
import tornadofx.uiComponent

class ReorderSceneDialogSteps(en: En, double: SoyleStoriesTestDouble) {

	companion object Driver : ApplicationTest() {

		private const val reorderRequestKey = ""

		fun setReorderRequest(double: SoyleStoriesTestDouble, request: Pair<Scene, Int>)
		{
			ProjectSteps.getProjectScope(double)?.get<WorkBench>()?.properties?.put(reorderRequestKey, request)
		}

		fun open(double: SoyleStoriesTestDouble) {
			val scenes = ScenesDriver.getCreatedScenes(double)
			setReorderRequest(double, scenes.first() to 2)
			val scope = ProjectSteps.getProjectScope(double)!!
			interact {
				scope.get<ReorderSceneDialog>()
				  .show(
					scenes.first().id.uuid.toString(),
					scenes.first().name,
					2
				  )
			}
		}

		fun reorderRequest(double: SoyleStoriesTestDouble): Pair<Scene, Int>?
		{
			return ProjectSteps.getProjectScope(double)?.get<WorkBench>()?.properties?.get(reorderRequestKey)
			  as? Pair<Scene, Int>
		}

		fun window(double: SoyleStoriesTestDouble) = listWindows().find {
				it.scene.root.uiComponent<ReorderSceneDialog>()?.app == double.application
			}?.takeIf { it.isShowing }

		fun isOpen(double: SoyleStoriesTestDouble): Boolean = window(double) != null

		fun dialog(double: SoyleStoriesTestDouble) = window(double)?.scene?.root as? DialogPane

		fun checkbox(double: SoyleStoriesTestDouble): CheckBox? {
			val dialog = dialog(double) ?: return null
			return from(dialog).lookup(".check-box").queryAllAs(CheckBox::class.java).firstOrNull()
		}

		fun buttonBar(double: SoyleStoriesTestDouble): ButtonBar? =
		  from(dialog(double)).lookup(".button-bar").queryAllAs(ButtonBar::class.java).firstOrNull()

		fun buttons(double: SoyleStoriesTestDouble) = buttonBar(double)?.buttons
	}

	init {
		with (en) {

			Given("the Confirm Reorder Scene Dialog has been opened") {
				if (! isOpen(double)) open(double)
			}

			When("the Confirm Reorder Scene Dialog {string} button is selected") { buttonLabel: String ->
				if (buttonLabel == "close") {
					interact {
						window(double)?.hide()
					}
				}
				else {
					val button = buttons(double)?.find { it is Button && it.text == buttonLabel }!!
					interact {
						button.fireEvent(ActionEvent())
					}
				}
			}
			When("the Confirm Reorder Scene Dialog do not show again check-box is checked") {
				val checkbox = checkbox(double)!!
				interact { checkbox.fire() }
			}


			Then("the Confirm Reorder Scene Dialog should be shown") {
				assertTrue(isOpen(double))
			}
			Then("the Confirm Reorder Scene Dialog should be closed") {
				assertFalse(isOpen(double))
			}
			Then("the Confirm Reorder Scene Dialog should not open the next time a Scene is reordered") {
				open(double)
				assertFalse(isOpen(double))
			}

		}
	}

}
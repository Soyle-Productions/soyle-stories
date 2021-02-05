package com.soyle.stories.storyevent

import com.soyle.stories.DependentProperty
import com.soyle.stories.project.ProjectSteps
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import com.soyle.stories.storyevent.CreateStoryEventDialogDriver.interact
import com.soyle.stories.storyevent.createStoryEventDialog.CreateStoryEventDialog
import com.soyle.stories.storyevent.createStoryEventDialog.createStoryEventDialog
import javafx.scene.control.TextInputControl
import javafx.scene.input.KeyCode
import org.testfx.framework.junit5.ApplicationTest
import tornadofx.Decorator
import tornadofx.decorators
import tornadofx.uiComponent

object CreateStoryEventDialogDriver : ApplicationTest() {

	val openDialog: DependentProperty<CreateStoryEventDialog> = object : DependentProperty<CreateStoryEventDialog> {
		override val dependencies: List<(SoyleStoriesTestDouble) -> Unit> = listOf(
		  ProjectSteps.Driver::checkProjectHasBeenOpened
		)

		override fun whenSet(double: SoyleStoriesTestDouble) {
			ProjectSteps.getProjectScope(double)!!.let {
				interact {
					createStoryEventDialog(it)
				}
			}
		}

		override fun get(double: SoyleStoriesTestDouble): CreateStoryEventDialog? {
			for (window in listWindows()) {
				val uiComponent = window.scene.root.uiComponent<CreateStoryEventDialog>()
				if (uiComponent != null && window.isShowing) {
					return uiComponent
				}
			}
			return null
		}
	}

	val invalidName = object : DependentProperty<TextInputControl> {
		override val dependencies: List<(SoyleStoriesTestDouble) -> Unit> = listOf(
		  openDialog::given
		)

		override fun get(double: SoyleStoriesTestDouble): TextInputControl? {
			val dialog = openDialog.get(double) ?: return null
			val textField = from(dialog.root).lookup(".text-field").queryTextInputControl()
			return textField.takeIf { it.isVisible && it.text.isBlank() }
		}

		override fun whenSet(double: SoyleStoriesTestDouble) {
			val dialog = openDialog.get(double)!!
			val textField = from(dialog.root).lookup(".text-field").queryTextInputControl()
			textField.text = ""
		}
	}

	val validName = object : DependentProperty<TextInputControl> {
		override val dependencies: List<(SoyleStoriesTestDouble) -> Unit> = listOf(
		  openDialog::given
		)

		override fun get(double: SoyleStoriesTestDouble): TextInputControl? {
			val dialog = openDialog.get(double) ?: return null
			val textField = from(dialog.root).lookup(".text-field").queryTextInputControl()
			return textField.takeIf { it.isVisible && it.text.isNotBlank() }
		}

		override fun whenSet(double: SoyleStoriesTestDouble) {
			val dialog = openDialog.get(double)!!
			val textField = from(dialog.root).lookup(".text-field").queryTextInputControl()
			textField.text = "Valid Story Event Name"
		}
	}

	val errorMessage = object : DependentProperty<Decorator> {
		override val dependencies: List<(SoyleStoriesTestDouble) -> Unit> = listOf(
		  invalidName::given
		)

		override fun get(double: SoyleStoriesTestDouble): Decorator? {
			val dialog = openDialog.get(double) ?: return null
			val textField = from(dialog.root).lookup(".text-field").queryTextInputControl()
			return textField.decorators.firstOrNull()
		}

		override fun whenSet(double: SoyleStoriesTestDouble) {
			interact {
				press(KeyCode.ENTER).release(KeyCode.ENTER)
			}
		}
	}

}
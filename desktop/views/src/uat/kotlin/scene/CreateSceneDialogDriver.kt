package com.soyle.stories.scene

import com.soyle.stories.project.ProjectSteps
import com.soyle.stories.scene.CreateSceneDialogDriver.interact
import com.soyle.stories.scene.createSceneDialog.CreateSceneDialog
import com.soyle.stories.scene.createSceneDialog.createSceneDialog
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import org.junit.jupiter.api.Assertions
import org.testfx.framework.junit5.ApplicationTest
import tornadofx.decorators
import tornadofx.uiComponent

object CreateSceneDialogDriver : ApplicationTest() {

	fun setIsOpen(double: SoyleStoriesTestDouble) {
		ProjectSteps.checkProjectHasBeenOpened(double)
		ProjectSteps.getProjectScope(double)!!.let {
			interact {
				createSceneDialog(it)
			}
		}
	}

	fun getIfOpen(double: SoyleStoriesTestDouble): CreateSceneDialog? {
		for (window in listWindows()) {
			val uiComponent = window.scene.root.uiComponent<CreateSceneDialog>()
			if (uiComponent != null && window.isShowing) {
				return uiComponent
			}
		}
		return null
	}

	fun isOpen(double: SoyleStoriesTestDouble): Boolean = getIfOpen(double) != null

	fun givenHasBeenOpened(double: SoyleStoriesTestDouble) {
		if (!isOpen(double)) {
			setIsOpen(double)
		}
		Assertions.assertTrue(isOpen(double))
	}

	fun setNameInputInvalid(double: SoyleStoriesTestDouble) {
		givenHasBeenOpened(double)
		val dialog = getIfOpen(double)!!
		val nameInput = from(dialog.root).lookup(".text-field").queryTextInputControl()
		nameInput.text = ""
	}

	fun isNameInputInvalid(double: SoyleStoriesTestDouble): Boolean {
		val dialog = getIfOpen(double) ?: return false
		val nameInput = from(dialog.root).lookup(".text-field").queryTextInputControl() ?: return false
		return nameInput.text.isBlank()
	}

	fun givenNameInputHasInvalidSceneName(double: SoyleStoriesTestDouble) {
		if (!isNameInputInvalid(double)) {
			setNameInputInvalid(double)
		}
		Assertions.assertTrue(isNameInputInvalid(double))
	}

	fun isErrorMessageShown(double: SoyleStoriesTestDouble): Boolean {
		val dialog = getIfOpen(double) ?: return false
		val nameInput = from(dialog.root).lookup(".text-field").queryTextInputControl() ?: return false
		return nameInput.decorators.isNotEmpty()
	}

	fun setNameInputValid(double: SoyleStoriesTestDouble) {
		givenHasBeenOpened(double)
		val dialog = getIfOpen(double)!!
		val nameInput = from(dialog.root).lookup(".text-field").queryTextInputControl()
		nameInput.text = "Valid Character Name"
	}

	fun isNameInputValid(double: SoyleStoriesTestDouble): Boolean {
		val dialog = getIfOpen(double) ?: return false
		val nameInput = from(dialog.root).lookup(".text-field").queryTextInputControl() ?: return false
		return nameInput.text.isNotBlank()
	}

	fun givenNameInputHasValidSceneName(double: SoyleStoriesTestDouble) {
		if (!isNameInputValid(double)) {
			setNameInputValid(double)
		}
		Assertions.assertTrue(isNameInputValid(double))
	}
}
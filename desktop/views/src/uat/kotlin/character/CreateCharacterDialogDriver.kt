package com.soyle.stories.character

import com.soyle.stories.character.CreateCharacterDialogDriver.interact
import com.soyle.stories.characterarc.createCharacterDialog.CreateCharacterDialog
import com.soyle.stories.characterarc.createCharacterDialog.createCharacterDialog
import com.soyle.stories.project.ProjectSteps
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import org.junit.jupiter.api.Assertions.assertTrue
import org.testfx.framework.junit5.ApplicationTest
import tornadofx.decorators
import tornadofx.uiComponent

object CreateCharacterDialogDriver : ApplicationTest() {

	fun setIsOpen(double: SoyleStoriesTestDouble) {
		ProjectSteps.checkProjectHasBeenOpened(double)
		ProjectSteps.getProjectScope(double)!!.let {
			interact {
				createCharacterDialog(it)
			}
		}
	}

	fun getIfOpen(double: SoyleStoriesTestDouble): CreateCharacterDialog? {
		for (window in listWindows()) {
			val uiComponent = window.scene.root.uiComponent<CreateCharacterDialog>()
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
		assertTrue(isOpen(double))
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

	fun givenNameInputHasInvalidCharacterName(double: SoyleStoriesTestDouble) {
		if (!isNameInputInvalid(double)) {
			setNameInputInvalid(double)
		}
		assertTrue(isNameInputInvalid(double))
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

	fun givenNameInputHasValidCharacterName(double: SoyleStoriesTestDouble) {
		if (!isNameInputValid(double)) {
			setNameInputValid(double)
		}
		assertTrue(isNameInputValid(double))
	}
}
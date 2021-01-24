package com.soyle.stories.writer

import com.soyle.stories.di.get
import com.soyle.stories.project.ProjectSteps
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import com.soyle.stories.writer.SettingsDialogSteps.Driver.interact
import com.soyle.stories.writer.settingsDialog.SettingsDialog
import io.cucumber.java8.En
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.testfx.framework.junit5.ApplicationTest
import tornadofx.uiComponent

class SettingsDialogSteps(en: En, double: SoyleStoriesTestDouble) {

	companion object Driver : ApplicationTest() {

		fun open(double: SoyleStoriesTestDouble) {
			val scope = ProjectSteps.getProjectScope(double)!!
			interact {
				scope.get<SettingsDialog>().show()
			}
		}

		fun window(double: SoyleStoriesTestDouble) = listWindows().find {
			it.scene.root.uiComponent<SettingsDialog>()?.app == double.application
		}?.takeIf { it.isShowing }

		fun isOpen(double: SoyleStoriesTestDouble): Boolean = window(double) != null

		fun options(double: SoyleStoriesTestDouble): List<CheckBox>?
		{
			return window(double)?.scene?.root?.let {
				from(it).lookup(".check-box").queryAll<CheckBox>().toList()
			}
		}
		fun buttons(double: SoyleStoriesTestDouble): List<Button>?
		{
			return window(double)?.scene?.root?.let {
				from(it).lookup(".button").queryAll<Button>().toList()
			}
		}
	}

	init {
		with(en) {

			Given("the Settings Dialog has been opened") {
				if (! isOpen(double)) open(double)
				assertTrue(isOpen(double))
			}
			Given("the {string} option has been toggled") { optionLabel: String ->
				val option = options(double)?.find { it.text == optionLabel }!!
				interact { option.fire() }
			}

			When("the Settings Dialog is opened") {
				open(double)
			}
			When("the {string} option is toggled") { optionLabel: String ->
				val option = options(double)?.find { it.text == optionLabel }!!
				interact { option.fire() }
			}
			When("the Settings Dialog {string} button is selected") { buttonLabel: String ->
				WriterSteps.dataSnapshot(double)
				val button = buttons(double)?.find { it.text == buttonLabel }!!
				interact { button.fire() }
			}

			Then("the Settings Dialog should be open") {
				assertTrue(isOpen(double))
			}
			Then("the Settings Dialog should be closed") {
				assertFalse(isOpen(double))
			}
			Then("the {string} option should be checked") { optionLabel: String ->
				val option = options(double)?.find { it.text == optionLabel }!!
				assertTrue(option.isSelected)
			}
			Then("the {string} option should not be checked") { optionLabel: String ->
				val option = options(double)?.find { it.text == optionLabel }!!
				assertFalse(option.isSelected)
			}
			Then("the Settings Dialog {string} button should be disabled") { buttonLabel: String ->
				val button = buttons(double)?.find { it.text == buttonLabel }!!
				assertTrue(button.isDisable)
			}
			Then("the Settings Dialog {string} button should not be disabled") { buttonLabel: String ->
				val button = buttons(double)?.find { it.text == buttonLabel }!!
				assertFalse(button.isDisable)
			}
		}
	}

}
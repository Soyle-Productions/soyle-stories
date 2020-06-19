package com.soyle.stories.writer

import com.soyle.stories.di.get
import com.soyle.stories.entities.Writer
import com.soyle.stories.project.ProjectSteps
import com.soyle.stories.soylestories.ApplicationScope
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import com.soyle.stories.writer.repositories.WriterRepository
import com.soyle.stories.writer.setDialogPreferences.SetDialogPreferencesController
import io.cucumber.java8.En
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

class WriterSteps(en: En, double: SoyleStoriesTestDouble) {

	companion object Driver {
		fun writer(double: SoyleStoriesTestDouble): Writer?
		{
			val scope = ProjectSteps.getProjectScope(double) ?: return null
			return runBlocking {
				scope.get<WriterRepository>().getWriterById(Writer.Id(scope.applicationScope.writerId))
			}
		}

		fun getDialogPreference(double: SoyleStoriesTestDouble, dialogType: DialogType): Boolean?
		{
			val appScope = (if (double.isStarted())
				double.application.scope as? ApplicationScope
			else null) ?: return false
			val scope = ProjectSteps.getProjectScope(double) ?: return false
			return runBlocking {
				scope.get<WriterRepository>().getWriterById(Writer.Id(appScope.writerId))?.preferences?.get("dialog.$dialogType")
				as? Boolean
			}
		}

		fun setDialogPreferences(double: SoyleStoriesTestDouble, dialogType: DialogType, shouldShow: Boolean)
		{
			val scope = ProjectSteps.getProjectScope(double)!!
			scope.get<SetDialogPreferencesController>()
			  .setDialogPreferences(dialogType.name, shouldShow)
		}

		fun givenDialogRequestedToBeHidden(double: SoyleStoriesTestDouble, dialogType: DialogType)
		{
			ProjectSteps.givenProjectHasBeenOpened(double)
			if (getDialogPreference(double, dialogType) != false) {
				setDialogPreferences(double, dialogType, false)
			}
			assertTrue(getDialogPreference(double, dialogType) == false)
		}

		fun givenDialogRequestedToBeShown(double: SoyleStoriesTestDouble, dialogType: DialogType)
		{
			ProjectSteps.givenProjectHasBeenOpened(double)
			if (getDialogPreference(double, dialogType) != true) {
				setDialogPreferences(double, dialogType, true)
			}
			assertTrue(getDialogPreference(double, dialogType) == true)
		}

		private var snapshot: Writer? = null
		fun dataSnapshot(double: SoyleStoriesTestDouble)
		{
			snapshot = writer(double)
		}

		fun toDialogType(uatLabel: String): DialogType?
		{
			return when(uatLabel) {
				"Confirm Reorder Scene Dialog" -> DialogType.ReorderScene
				"Confirm Delete Scene Dialog" -> DialogType.DeleteScene
				"Confirm Delete Theme Dialog" -> DialogType.DeleteTheme
				else -> null
			}
		}
	}

	init {

		SettingsDialogSteps(en, double)

		with(en) {

			Given("the user has requested to not be shown the Confirm Reorder Scene Dialog") {
				givenDialogRequestedToBeHidden(double, DialogType.ReorderScene)
			}

			Given("the {string} has been requested to be hidden") { dialogType: String ->
				givenDialogRequestedToBeHidden(double, toDialogType(dialogType)!!)
			}
			Given("the {string} has not been requested to be hidden") { dialogType: String ->
				givenDialogRequestedToBeShown(double, toDialogType(dialogType)!!)
			}

			Then("no changes should be made to the user dialog preferences") {
				assertEquals(snapshot!!, writer(double))
			}
			Then("the {string} should be requested to be hidden") { dialogType: String ->
				val preference = getDialogPreference(double, toDialogType(dialogType)!!)
				assertTrue(preference == false)
			}
			Then("the {string} should not be requested to be hidden") { dialogType: String ->
				val preference = getDialogPreference(double, toDialogType(dialogType)!!)
				assertTrue(preference == true)
			}
		}

	}

}
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
import org.junit.jupiter.api.Assertions.assertTrue

class WriterSteps(en: En, double: SoyleStoriesTestDouble) {

	companion object Driver {
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
	}

	init {

		with(en) {

			Given("the user has requested to not be shown the Confirm Reorder Scene Dialog") {
				if (getDialogPreference(double, DialogType.ReorderScene) != false) setDialogPreferences(double, DialogType.ReorderScene, false)
				assertTrue(getDialogPreference(double, DialogType.ReorderScene) == false)
			}

		}

	}

}
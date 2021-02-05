package com.soyle.stories.project

import com.soyle.stories.project.startProjectDialog.StartProjectDialog
import com.soyle.stories.soylestories.ApplicationScope
import com.soyle.stories.soylestories.ApplicationSteps
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import javafx.scene.Parent
import org.testfx.framework.junit5.ApplicationTest
import tornadofx.uiComponent

object CreateProjectDialogDriver: ApplicationTest() {

	fun getIfOpen(double: SoyleStoriesTestDouble): StartProjectDialog?
	{
		val app = ApplicationSteps.getStartedApplication(double) ?: return null
		val scope = app.scope as? ApplicationScope ?: return null
		for (window in listTargetWindows()) {
			val dialog = window.scene.root.uiComponent<StartProjectDialog>() ?: continue
			return dialog
		}
		return null
	}

	fun isOpen(double: SoyleStoriesTestDouble): Boolean =
	  getIfOpen(double) != null

}
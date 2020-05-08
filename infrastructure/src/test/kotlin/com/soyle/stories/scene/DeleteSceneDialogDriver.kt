package com.soyle.stories.scene

import com.soyle.stories.project.ProjectSteps
import com.soyle.stories.scene.items.SceneItemViewModel
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import javafx.scene.Parent
import javafx.scene.control.DialogPane
import org.testfx.framework.junit5.ApplicationTest

object DeleteSceneDialogDriver : ApplicationTest() {

	fun getIfOpen(double: SoyleStoriesTestDouble): Parent?
	{
		ProjectSteps.getProjectScope(double) ?: return null
		return listTargetWindows().find {
			val styleClass = it.scene?.root?.styleClass ?: return@find false

			styleClass.contains("deleteScene")
		}?.scene?.root
	}

	fun isOpen(double: SoyleStoriesTestDouble): Boolean =
	  getIfOpen(double) != null

	fun isShowingNameOf(double: SoyleStoriesTestDouble, scene: SceneItemViewModel): Boolean
	{
		val dialog = getIfOpen(double) as? DialogPane ?: return false
		return dialog.headerText.contains(scene.name)
	}

}
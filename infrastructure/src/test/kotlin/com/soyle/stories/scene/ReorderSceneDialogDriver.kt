package com.soyle.stories.scene

import com.soyle.stories.scene.reorderSceneDialog.ReorderSceneDialog
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import org.testfx.framework.junit5.ApplicationTest
import tornadofx.uiComponent

object ReorderSceneDialogDriver : ApplicationTest() {

	fun isDialogOpen(double: SoyleStoriesTestDouble): Boolean
	{
		for (window in CreateSceneDialogDriver.listWindows()) {
			val uiComponent = window.scene.root.uiComponent<ReorderSceneDialog>()
			if (uiComponent != null && window.isShowing) {
				return true
			}
		}
		return false
	}

}
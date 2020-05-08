package com.soyle.stories.scene

import com.soyle.stories.project.ProjectSteps
import com.soyle.stories.scene.SceneListDriver.interact
import com.soyle.stories.scene.sceneList.SceneList
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import com.soyle.stories.testutils.findComponentsInScope
import javafx.scene.control.MenuItem
import org.junit.jupiter.api.Assertions.assertTrue
import org.testfx.framework.junit5.ApplicationTest

object SceneListDriver : ApplicationTest() {

	fun setOpen(double: SoyleStoriesTestDouble)
	{
		ProjectSteps.givenProjectHasBeenOpened(double)
		whenOpened(double)
	}

	fun getIfOpen(double: SoyleStoriesTestDouble): SceneList?
	{
		val projectScope = ProjectSteps.getProjectScope(double) ?: return null
		return findComponentsInScope<SceneList>(projectScope).singleOrNull()?.takeIf { it.currentStage?.isShowing == true }
	}

	fun isOpen(double: SoyleStoriesTestDouble): Boolean = getIfOpen(double) != null

	fun whenOpened(double: SoyleStoriesTestDouble) {
		val menuItem: MenuItem = ProjectSteps.getMenuItem(double, "tools", "tools_Scenes")!!
		interact {
			menuItem.fire()
		}
	}

	fun givenHasBeenOpened(double: SoyleStoriesTestDouble) {
		if (! isOpen(double))
		{
			setOpen(double)
		}
		assertTrue(isOpen(double))

	}

}
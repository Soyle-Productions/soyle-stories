package com.soyle.stories.scene

import com.soyle.stories.entities.Scene
import com.soyle.stories.project.ProjectSteps
import com.soyle.stories.scene.SceneListDriver.interact
import com.soyle.stories.scene.items.SceneItemViewModel
import com.soyle.stories.scene.sceneList.SceneList
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import com.soyle.stories.testutils.findComponentsInScope
import javafx.scene.Node
import javafx.scene.control.MenuItem
import javafx.scene.control.TreeView
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.testfx.framework.junit5.ApplicationTest

object SceneListDriver : ApplicationTest() {

	fun setOpen(double: SoyleStoriesTestDouble)
	{
		ProjectSteps.givenProjectHasBeenOpened(double)
		whenOpened(double)
	}

	fun setClosed(double: SoyleStoriesTestDouble)
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

	fun givenHasBeenClosed(double: SoyleStoriesTestDouble)
	{
		if (isOpen(double))
		{
			setClosed(double)
		}
		assertFalse(isOpen(double))
	}

	fun isShowingEmptyMessage(double: SoyleStoriesTestDouble): Boolean
	{
		val list = getIfOpen(double) ?: return false
		val emptyDisplay = from(list.root).lookup(".empty-display").queryAll<Node>().firstOrNull() ?: return false
		return emptyDisplay.visibleProperty().value
	}

	fun isShowingNumberOfScenes(double: SoyleStoriesTestDouble, count: Int): Boolean
	{
		val list = getIfOpen(double) ?: return false
		val treeView = from(list.root).lookup(".tree-view").queryAll<TreeView<*>>().firstOrNull() ?: return false
		return treeView.root.children.size == count
	}

	fun isShowingScene(double: SoyleStoriesTestDouble, scene: Scene): Boolean
	{
		val list = getIfOpen(double) ?: return false
		val treeView = from(list.root).lookup(".tree-view").queryAll<TreeView<*>>().firstOrNull() ?: return false
		val items = treeView.root.children.map { it.value }.filterIsInstance<SceneItemViewModel>()
		return items.find {
			it.id == scene.id.uuid.toString() &&
			  it.name == scene.name
		} != null
	}

}
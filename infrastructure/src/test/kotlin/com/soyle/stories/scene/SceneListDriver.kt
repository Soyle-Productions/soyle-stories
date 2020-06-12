package com.soyle.stories.scene

import com.soyle.stories.ReadOnlyDependentProperty
import com.soyle.stories.UATLogger
import com.soyle.stories.common.async
import com.soyle.stories.common.editingCell
import com.soyle.stories.di.get
import com.soyle.stories.entities.Scene
import com.soyle.stories.project.ProjectSteps
import com.soyle.stories.project.layout.LayoutViewListener
import com.soyle.stories.scene.SceneListDriver.interact
import com.soyle.stories.scene.items.SceneItemViewModel
import com.soyle.stories.scene.sceneList.SceneList
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import com.soyle.stories.testutils.findComponentsInScope
import javafx.geometry.Side
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.input.MouseButton
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.testfx.framework.junit5.ApplicationTest
import tornadofx.selectFirst

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
		return findComponentsInScope<SceneList>(projectScope).singleOrNull()?.takeIf {
			it.currentStage?.isShowing == true
		}
	}

	fun isOpen(double: SoyleStoriesTestDouble): Boolean = getIfOpen(double) != null

	fun whenOpened(double: SoyleStoriesTestDouble) {
		val scope = ProjectSteps.getProjectScope(double)!!
		interact {
			async(scope) {
				scope.get<LayoutViewListener>().toggleToolOpen(com.soyle.stories.layout.config.fixed.SceneList)
			}
		}
	}

	fun whenClosed(double: SoyleStoriesTestDouble) {
		val scope = ProjectSteps.getProjectScope(double)!!
		interact {
			async(scope) {
				scope.get<LayoutViewListener>().toggleToolOpen(com.soyle.stories.layout.config.fixed.SceneList)
			}
		}
	}

	fun givenHasBeenOpened(double: SoyleStoriesTestDouble) {
		UATLogger.log("Given Scene List Has Been Opened")
		if (! isOpen(double))
		{
			setOpen(double)
		}
		assertTrue(isOpen(double))
	}

	fun givenHasBeenClosed(double: SoyleStoriesTestDouble)
	{
		UATLogger.log("Given Scene List Has Been Closed")
		if (isOpen(double))
		{
			setClosed(double)
		}
		assertFalse(isOpen(double))
	}

	fun setVisible(double: SoyleStoriesTestDouble) {
		givenHasBeenOpened(double)
		getIfOpen(double)?.owningTab?.let {
			it.tabPane.selectionModel.select(it)
		}
	}

	fun getIfVisible(double: SoyleStoriesTestDouble): SceneList? =
	  getIfOpen(double)?.takeIf { it.owningTab?.isSelected == true }

	fun isVisible(double: SoyleStoriesTestDouble): Boolean =
	  getIfVisible(double) != null

	fun givenHasBeenVisible(double: SoyleStoriesTestDouble)
	{
		UATLogger.log("Given Scene List Has Been Visible")
		if (! isVisible(double))
		{
			setVisible(double)
		}
		assertTrue(isVisible(double))
	}

	fun isShowingEmptyMessage(double: SoyleStoriesTestDouble): Boolean
	{
		val list = getIfOpen(double) ?: return false
		val emptyDisplay = from(list.root).lookup(".empty-display").queryAll<Node>().firstOrNull() ?: return false
		return emptyDisplay.visibleProperty().value
	}

	val centerButton = object : ReadOnlyDependentProperty<Button> {
		override fun get(double: SoyleStoriesTestDouble): Button? {
			val list = getIfOpen(double) ?: return null
			val emptyDisplay = from(list.root).lookup(".empty-display").queryAll<Node>().firstOrNull() ?: return null
			if (! emptyDisplay.visibleProperty().get()) return null
			return from(emptyDisplay).lookup(".center-button").queryAll<Button>().firstOrNull()
		}
	}

	fun setTreeViewVisible(double: SoyleStoriesTestDouble)
	{
		givenHasBeenOpened(double)
		givenHasBeenVisible(double)
		ScenesDriver.givenNumberOfCreatedScenesIsAtLeast(double, 1)
	}

	fun getTreeViewIfVisible(double: SoyleStoriesTestDouble): TreeView<SceneItemViewModel>?
	{
		val list = getIfVisible(double) ?: return (null).also { UATLogger.log("scene list not visible") }
		val treeView = from(list.root).lookup(".tree-view").queryAll<TreeView<*>>().firstOrNull()
		  ?: return (null).also { UATLogger.log("did not find tree view") }
		val castTreeView = treeView as? TreeView<SceneItemViewModel>
		if (castTreeView == null) UATLogger.log("tree view cannot be cast to TreeView<SceneItemViewModel>")
		val visibleCastTreeView = castTreeView?.takeIf { it.visibleProperty().value }
		if (visibleCastTreeView == null) UATLogger.log("tree view not visible")
		return visibleCastTreeView
	}

	fun isTreeViewVisible(double: SoyleStoriesTestDouble): Boolean = getTreeViewIfVisible(double) != null

	fun givenTreeViewIsVisible(double: SoyleStoriesTestDouble)
	{
		UATLogger.log("Given Scene List Tree View Has Been Visible")
		if (! isTreeViewVisible(double))
		{
			setTreeViewVisible(double)
		}
		assertTrue(isTreeViewVisible(double))
	}

	fun getItems(double: SoyleStoriesTestDouble): List<TreeItem<SceneItemViewModel?>>
	{
		val treeView = getTreeViewIfVisible(double) ?: return emptyList()
		return treeView.root.children
	}

	fun isShowingNumberOfScenes(double: SoyleStoriesTestDouble, count: Int): Boolean
	{
		val items = getItems(double)
		UATLogger.log("Item count: " + items.size)
		return items.size == count
	}

	fun isShowingScene(double: SoyleStoriesTestDouble, scene: Scene): Boolean
	{
		val items = getItems(double)
		if (items.isEmpty()) return (false).also { UATLogger.log("no items in scene list") }
		return items.find {
			val idsMatch = it.value?.id == scene.id.uuid.toString()
			val namesMatch = it.value?.name == scene.name
			if (idsMatch && !namesMatch) UATLogger.log("Matching id but mismatched name")
			idsMatch && namesMatch
		} != null
	}

	fun indexOfItemWithId(double: SoyleStoriesTestDouble, id: Scene.Id): Int
	{
		val items = getItems(double)
		return items.indexOfFirst {
			it.value?.id == id.uuid.toString()
		}
	}

	fun setItemSelected(double: SoyleStoriesTestDouble)
	{
		givenTreeViewIsVisible(double)
		val treeView = getTreeViewIfVisible(double)!!
		interact {
			treeView.selectFirst()
		}
	}

	fun getSelectedItem(double: SoyleStoriesTestDouble): SceneItemViewModel?
	{
		val treeView = getTreeViewIfVisible(double) ?: return (null).also { UATLogger.log("tree view not visible") }
		var item: SceneItemViewModel? = null
		interact {
			item = treeView.selectionModel?.selectedItem?.value
		}
		if (item == null) UATLogger.log("no item selected")
		return item
	}

	fun isItemSelected(double: SoyleStoriesTestDouble): Boolean =
	  getSelectedItem(double) != null

	fun givenASceneHasBeenSelected(double: SoyleStoriesTestDouble)
	{
		UATLogger.log("Given A Scene Has Been Selected")
		if (! isItemSelected(double))
		{
			setItemSelected(double)
		}
		assertTrue(isItemSelected(double))
	}

	fun setRightClickMenuOpen(double: SoyleStoriesTestDouble)
	{
		givenASceneHasBeenSelected(double)
		val treeView = getTreeViewIfVisible(double)!!
		interact {
			treeView.contextMenu.show(treeView, Side.TOP, 0.0, 0.0)
		}
	}

	fun isRightClickMenuOpen(double: SoyleStoriesTestDouble): Boolean
	{
		val treeView = getTreeViewIfVisible(double) ?: return false
		return treeView.contextMenu?.isShowing == true
	}

	fun givenRightClickMenuHasBeenOpened(double: SoyleStoriesTestDouble)
	{
		UATLogger.log("Given Scene List Right Click Menu Has Been Opened")
		if (! isRightClickMenuOpen(double))
		{
			setRightClickMenuOpen(double)
		}
		assertTrue(isRightClickMenuOpen(double))
	}

	fun setRenameInputBoxVisible(double: SoyleStoriesTestDouble)
	{
		givenTreeViewIsVisible(double)
		givenASceneHasBeenSelected(double)
		val treeView = getTreeViewIfVisible(double)!!
		interact {
			treeView.edit(getItems(double).first())
		}
	}

	fun getRenameInputBoxIfVisible(double: SoyleStoriesTestDouble): TextField?
	{
		val treeView = getTreeViewIfVisible(double) ?: return null
		return treeView.editingCell?.graphic as? TextField
	}

	fun isRenameInputBoxVisible(double: SoyleStoriesTestDouble): Boolean =
	  getRenameInputBoxIfVisible(double) != null

	fun isRenameInputBoxShowingNameOfSelected(double: SoyleStoriesTestDouble): Boolean {
		val inputBox = getRenameInputBoxIfVisible(double) ?: return false
		val selectedItem = getSelectedItem(double) ?: return false
		return inputBox.text == selectedItem.name
	}

	fun givenRenameInputBoxHasBeenVisible(double: SoyleStoriesTestDouble)
	{
		if (! isRenameInputBoxVisible(double))
		{
			setRenameInputBoxVisible(double)
		}
		assertTrue(isRenameInputBoxVisible(double))
	}

	fun setValidSceneNameEntered(double: SoyleStoriesTestDouble)
	{
		givenRenameInputBoxHasBeenVisible(double)
		val inputBox = getRenameInputBoxIfVisible(double)!!
		inputBox.textProperty().set("Valid Scene Name")
	}

	fun isValidSceneNameEntered(double: SoyleStoriesTestDouble): Boolean {
		val inputBox = getRenameInputBoxIfVisible(double) ?: return false
		val selectedItem = getSelectedItem(double) ?: return false
		val text = inputBox.text
		return text.isNotBlank() && text != selectedItem.name
	}


	fun givenValidSceneNameHasBeenEntered(double: SoyleStoriesTestDouble)
	{
		if (! isValidSceneNameEntered(double))
		{
			setValidSceneNameEntered(double)
		}
		assertTrue(isValidSceneNameEntered(double))
	}

	fun whenRightClickOptionIsClicked(double: SoyleStoriesTestDouble, rightClickOptionText: String)
	{
		val treeView = getTreeViewIfVisible(double) ?: error("no visible tree view")
		val menu = treeView.contextMenu?.takeIf { it.isShowing } ?: error("no visible right-click menu")
		val item = menu.items.find { it.text == rightClickOptionText } ?: error("no menu item with id $rightClickOptionText")
		interact {
			item.fire()
		}
	}

	fun isSelectedItemNameMatching(double: SoyleStoriesTestDouble, testName: String): Boolean
	{
		val item = getSelectedItem(double) ?: return (false).also { UATLogger.log("no selected item") }
		return item.name == testName
	}

	fun whenBottomButtonIsClicked(double: SoyleStoriesTestDouble, buttonId: String)
	{
		val list = getIfOpen(double) ?: error("scene list is not open")
		interact {
			clickOn(from(list.root).lookup("#actionBar_$buttonId").queryButton(), MouseButton.PRIMARY)
		}
	}

	fun whenASceneIsDragged(double: SoyleStoriesTestDouble)
	{
		val list = getTreeViewIfVisible(double) ?: error("scene list is not open")
		interact {
			val screenBounds = list.localToScreen(list.boundsInLocal)
			moveTo(screenBounds.minX + 6, screenBounds.minY + 6)
			  .press(MouseButton.PRIMARY)
			  .drag(screenBounds.minX + 6, screenBounds.minY + 60, MouseButton.PRIMARY)
			  .release(MouseButton.PRIMARY)
		}
	}

}
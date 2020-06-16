package com.soyle.stories.location

import com.soyle.stories.common.async
import com.soyle.stories.common.editingCell
import com.soyle.stories.di.get
import com.soyle.stories.entities.Location
import com.soyle.stories.location.LocationListDriver.interact
import com.soyle.stories.location.items.LocationItemViewModel
import com.soyle.stories.location.locationList.LocationList
import com.soyle.stories.location.repositories.LocationRepository
import com.soyle.stories.project.ProjectSteps
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import com.soyle.stories.testutils.findComponentsInScope
import javafx.scene.Node
import javafx.scene.control.TextField
import javafx.scene.control.TreeView
import org.junit.jupiter.api.Assertions.assertTrue
import org.testfx.framework.junit5.ApplicationTest
import java.util.*

object LocationListDriver : ApplicationTest() {

	fun setRenameInputBoxVisible(double: SoyleStoriesTestDouble)
	{
		LocationSteps.givenLocationIsSelectedInLocationListTool(double)
		val scope = ProjectSteps.getProjectScope(double)!!
		interact {
			val locationList = scope.get<LocationList>()
			locationList.owningTab?.let {
				it.tabPane.selectionModel.select(it)
			}
			val treeView = from(locationList.root).lookup(".tree-view").query<TreeView<*>>() as TreeView<LocationItemViewModel?>
			treeView.edit(treeView.selectionModel.selectedItem)
		}
	}

	fun getRenameInputBox(double: SoyleStoriesTestDouble): TextField?
	{
		val projectScope = ProjectSteps.getProjectScope(double) ?: return null
		val locationList = findComponentsInScope<LocationList>(projectScope).singleOrNull() ?: return null
		var graphic: Node? = null
		interact {
			graphic = from(locationList.root).lookup(".tree-view").query<TreeView<*>>().editingCell?.graphic
		}
		return graphic as? TextField
	}

	fun isRenameInputBoxVisible(double: SoyleStoriesTestDouble): Boolean
	{
		return getRenameInputBox(double)?.takeIf { it.isVisible } != null
	}

	fun givenRenameInputBoxIsVisible(double: SoyleStoriesTestDouble)
	{
		if (! isRenameInputBoxVisible(double))
		{
			setRenameInputBoxVisible(double)
		}
		assertTrue(isRenameInputBoxVisible(double))
	}

	fun isLocationShowingStoredName(double: SoyleStoriesTestDouble): Boolean
	{
		val selectedItem = LocationSteps.getLocationSelectedInLocationListTool(double) ?: return false
		val scope = ProjectSteps.getProjectScope(double) ?: return false
		var storedItem: Location? = null
		async(scope.applicationScope) {
			storedItem = scope.get<LocationRepository>().getLocationById(Location.Id(UUID.fromString(selectedItem.id)))
		}
		return selectedItem.name == storedItem!!.name
	}
}
package com.soyle.stories.location

import com.soyle.stories.di.DI
import com.soyle.stories.di.get
import com.soyle.stories.layout.tools.fixed.FixedTool
import com.soyle.stories.location.items.LocationItemViewModel
import com.soyle.stories.location.locationList.LocationList
import com.soyle.stories.location.locationList.LocationListModel
import com.soyle.stories.location.locationList.LocationListViewListener
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.layout.Dialog
import com.soyle.stories.project.layout.LayoutViewListener
import com.soyle.stories.project.projectList.ProjectFileViewModel
import com.soyle.stories.soylestories.ApplicationScope
import javafx.event.ActionEvent
import javafx.scene.control.TreeView
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.testfx.framework.junit5.ApplicationTest
import tornadofx.selectFirst
import java.util.*
import kotlin.reflect.KClass

class LocationListUnitTest : ApplicationTest() {

	@Test
	fun `contextMenu open option calls openLocationDetails`() {
		val applicationScope = ApplicationScope()
		val projectScope = ProjectScope(applicationScope, ProjectFileViewModel(UUID.randomUUID(), "", ""))
		var openLocationDetailsLocationId: String? = null

		DI.registerTypeFactory<LayoutViewListener> {
			object : LayoutViewListener {
				override fun loadLayoutForProject(projectId: UUID) {

				}

				override suspend fun toggleToolOpen(tool: FixedTool) {

				}

				override suspend fun closeTool(toolId: String) {
				}

				override fun openDialog(dialog: Dialog) {
				}

				override fun closeDialog(dialog: KClass<out Dialog>) {
				}

			}
		}
		DI.registerTypeFactory<LocationListViewListener> {
			object : LocationListViewListener {
				override fun getValidState() {
				}

				override fun openLocationDetails(locationId: String) {
					openLocationDetailsLocationId = locationId
				}

				override fun renameLocation(locationId: String, newName: String) {

				}
			}
		}

		val locationList = projectScope.get<LocationList>()
		val firstLocationId = UUID.randomUUID().toString()
		projectScope.get<LocationListModel>().locations.add(LocationItemViewModel(firstLocationId, ""))
		interact {
			val treeView = (locationList.root.lookup(".tree-view") as TreeView<*>)
			treeView.selectFirst()
			val menuItem = treeView.contextMenu!!.items.find { it.id == "open" }
			  ?: error("No menu item with id open")
			(menuItem.onAction ?: error("no action registered")).handle(ActionEvent())
		}

		assertEquals(firstLocationId, openLocationDetailsLocationId)

	}

}
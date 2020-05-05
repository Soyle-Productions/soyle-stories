package com.soyle.stories.project

import com.mongodb.internal.connection.tlschannel.util.Util.assertTrue
import com.soyle.stories.common.async
import com.soyle.stories.di.get
import com.soyle.stories.project.ProjectSteps.interact
import com.soyle.stories.project.projectList.ProjectListViewListener
import com.soyle.stories.soylestories.ApplicationScope
import com.soyle.stories.soylestories.ApplicationSteps
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import javafx.scene.layout.BorderPane
import org.testfx.framework.junit5.ApplicationTest

object ProjectSteps : ApplicationTest() {

	/**
	 * project opened
	 */

	/**
	 * opens the project in the application double and sets any needed dependencies to make it work
	 */
	fun setProjectHasBeenOpened(double: SoyleStoriesTestDouble) {
		ApplicationSteps.givenApplicationHasBeenStarted(double)
		whenProjectIsOpened(double)
	}

	fun getProjectScope(double: SoyleStoriesTestDouble): ProjectScope? {
		val app = ApplicationSteps.getStartedApplication(double) ?: return null
		return (app.scope as? ApplicationScope)?.projectScopes?.firstOrNull()
	}

	fun isProjectOpened(double: SoyleStoriesTestDouble): Boolean = getProjectScope(double) != null

	/**
	 * opens the project in the application double and fails if needed dependencies are not available
	 */
	fun whenProjectIsOpened(double: SoyleStoriesTestDouble) {
		val application = ApplicationSteps.getStartedApplication(double) ?: error("application not yet started")
		val applicationScope = application.scope as? ApplicationScope ?: error("application does not have correct scope")
		interact {
			val viewListener = applicationScope.get<ProjectListViewListener>()
			async(applicationScope) {
				viewListener.startNewProject("D:\\Brendan\\Documents", "Untitled")
			}
		}
	}

	fun givenProjectHasBeenOpened(double: SoyleStoriesTestDouble) {
		if (! isProjectOpened(double)) {
			setProjectHasBeenOpened(double)
		}
		assertTrue(isProjectOpened(double))
	}

	/**
	 *
	 * menu item selection
	 *
	 */

	/**
	 *
	 */
	fun setSelectedMenuItem(double: SoyleStoriesTestDouble, menuId: String, vararg menuItemIds: String) {
		givenProjectHasBeenOpened(double)
		whenMenuItemIsSelected(double, menuId, *menuItemIds)
	}

	fun getMenuItem(double: SoyleStoriesTestDouble, menuId: String, vararg menuItemIds: String): MenuItem? {
		val projectScope = getProjectScope(double) ?: return null
		if (menuItemIds.isEmpty()) return null
		var menuItem: MenuItem? = null
		interact {
			val menuBar = ((projectScope.get<WorkBench>().root as BorderPane).top as MenuBar)
			val topMenu = menuBar.menus.find { it.id == menuId } ?: return@interact
			topMenu.show()
			var foundItem: MenuItem? = null
			var currentItemList = topMenu.items
			menuItemIds.forEach { menuItemId ->
				val item = currentItemList?.find { it.id == menuItemId } ?: return@interact
				foundItem = item
				currentItemList = (item as? Menu)?.let {
					it.show()
					it.items
				}
			}
			menuItem = foundItem?.takeIf { it.id == menuItemIds.last() }
		}
		return menuItem
	}

	fun whenMenuItemIsSelected(double: SoyleStoriesTestDouble, menuId: String, vararg menuItemIds: String) {
		val menuItem = getMenuItem(double, menuId, *menuItemIds) ?: error("No menu item with id ${menuItemIds.lastOrNull()}")
		interact {
			menuItem.fire()
		}
	}

	fun whenUserClicksAway(double: SoyleStoriesTestDouble)
	{
		val scope = getProjectScope(double)!!
		interact {
			scope.get<WorkBench>().root.requestFocus()
		}
	}



}
package com.soyle.stories.project

import com.soyle.stories.UATLogger
import com.soyle.stories.common.async
import com.soyle.stories.di.get
import com.soyle.stories.project.ProjectSteps.Driver.interact
import com.soyle.stories.project.projectList.ProjectListViewListener
import com.soyle.stories.soylestories.ApplicationScope
import com.soyle.stories.soylestories.ApplicationSteps
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import io.cucumber.java8.En
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import javafx.scene.layout.BorderPane
import org.junit.jupiter.api.Assertions.assertTrue
import org.testfx.framework.junit5.ApplicationTest

class ProjectSteps(en: En, double: SoyleStoriesTestDouble) {

	companion object Driver : ApplicationTest() {

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

		fun checkProjectHasBeenOpened(double: SoyleStoriesTestDouble): Unit {
			getProjectScope(double) ?: run {
				setProjectHasBeenOpened(double)
				getProjectScope(double)!!
			}
		}

		fun givenProjectHasBeenOpened(double: SoyleStoriesTestDouble): ProjectScope {
			return getProjectScope(double) ?: run {
				setProjectHasBeenOpened(double)
				getProjectScope(double)!!
			}
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
			checkProjectHasBeenOpened(double)
			whenMenuItemIsSelected(double, menuId, *menuItemIds)
		}

		fun getMenuItem(double: SoyleStoriesTestDouble, menuName: String, vararg menuItemNames: String): MenuItem? {
			val projectScope = getProjectScope(double) ?: return (null).also { UATLogger.log("project not started") }
			var menuItem: MenuItem? = null
			interact {
				val menuBar = ((projectScope.get<WorkBench>().root as BorderPane).top as MenuBar)
				val topMenu = menuBar.menus.find { it.text == menuName } ?: return@interact UATLogger.log("did not find $menuName in ${menuBar.menus}")
				topMenu.show()
				if (menuItemNames.isEmpty()) return@interact
				var foundItem: MenuItem? = null
				var currentItemList = topMenu.items
				menuItemNames.forEach { menuItemName ->
					val item = currentItemList?.find { it.text == menuItemName } ?: return@interact UATLogger.log("did not find $menuItemName in $currentItemList")
					foundItem = item
					currentItemList = (item as? Menu)?.let {
						it.show()
						it.items
					}
				}
				menuItem = foundItem?.takeIf { it.text == menuItemNames.last() }
				if (menuItem == null) {
					UATLogger.log("item id: ${foundItem?.text} does not match last menuItemId: ${menuItemNames.last()}")
				}
			}
			return menuItem ?: (null).also { UATLogger.log("menu item is null") }
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

	init {
		with(en) {

			When("the File Menu is opened") {
				getMenuItem(double, "File")
			}
			When("the File Menu {string} item is selected") { itemName: String ->
				whenMenuItemIsSelected(double, "File", itemName)
			}

			Then("the File Menu should display {string}") { itemName: String ->
				getMenuItem(double, "File", itemName)!!
			}

		}
	}



}
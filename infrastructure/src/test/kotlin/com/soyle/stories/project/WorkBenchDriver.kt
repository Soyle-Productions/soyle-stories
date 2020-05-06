package com.soyle.stories.project

import com.soyle.stories.di.get
import com.soyle.stories.project.WorkBenchDriver.interact
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import org.junit.jupiter.api.Assertions.assertTrue
import org.testfx.framework.junit5.ApplicationTest

object WorkBenchDriver : ApplicationTest() {

	fun setMenuOpen(double: SoyleStoriesTestDouble, menuId: String, vararg menuIds: String)
	{
		ProjectSteps.givenProjectHasBeenOpened(double)
		whenMenuIsOpened(double, menuId, *menuIds)
	}

	fun getMenu(double: SoyleStoriesTestDouble, menuId: String, vararg menuIds: String): Menu? {
		val projectScope = ProjectSteps.getProjectScope(double) ?: return null
		val menuBar = from(projectScope.get<WorkBench>().root).lookup(".menu-bar").queryAll<MenuBar>().firstOrNull()
		  ?: return null
		val topMenu = menuBar.menus.find { it.id == menuId } ?: return null
		if (menuIds.isEmpty()) return topMenu
		var menu: Menu = topMenu
		menuIds.forEach { currentMenuId ->
			val item = menu.items.find { it.id == currentMenuId } ?: return null
			if (item is Menu) menu = item
		}
		return menu
	}

	fun isMenuOpen(double: SoyleStoriesTestDouble, menuId: String, vararg menuIds: String): Boolean
	{
		val menu = getMenu(double, menuId, *menuIds) ?: return false
		return menu.isShowing
	}

	fun whenMenuIsOpened(double: SoyleStoriesTestDouble, menuId: String, vararg menuIds: String) {
		val projectScope = ProjectSteps.getProjectScope(double)!!
		val menuBar = from(projectScope.get<WorkBench>().root).lookup(".menu-bar").query<MenuBar>()
		val topMenu = menuBar.menus.find { it.id == menuId }!!
		interact {
			topMenu.show()
			if (menuIds.isEmpty()) return@interact
			var menu: Menu = topMenu
			menuIds.forEach { currentMenuId ->
				val item = menu.items.find { it.id == currentMenuId } ?: return@interact
				if (item is Menu) {
					item.show()
					menu = item
				}
			}
		}
	}

	fun givenMenuHasBeenOpened(double: SoyleStoriesTestDouble, menuId: String, vararg menuIds: String)
	{
		if (! isMenuOpen(double, menuId, *menuIds))
		{
			setMenuOpen(double, menuId, *menuIds)
		}
		assertTrue(isMenuOpen(double, menuId, *menuIds))
	}

	fun getMenuItem(double: SoyleStoriesTestDouble, menuId: String, vararg menuIds: String, menuItemText: String): MenuItem?
	{
		val menu = getMenu(double, menuId, *menuIds) ?: return null
		return menu.items.find { it.text == menuItemText }
	}

	fun isMenuItemVisible(double: SoyleStoriesTestDouble, menuId: String, vararg menuIds: String, menuItemText: String): Boolean
	{
		val item = getMenuItem(double, menuId, *menuIds, menuItemText = menuItemText) ?: return false
		return item.isVisible
	}

	fun whenMenuItemIsSelected(double: SoyleStoriesTestDouble, menuId: String, vararg menuIds: String, menuItemText: String)
	{
		whenMenuIsOpened(double, menuId, *menuIds)
		val item = getMenuItem(double, menuId, *menuIds, menuItemText = menuItemText)!!
		interact {
			item.fire()
		}
	}
}
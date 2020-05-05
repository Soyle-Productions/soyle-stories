package com.soyle.stories.character

import com.soyle.stories.character.CharacterSteps.interact
import com.soyle.stories.characterarc.characterList.*
import com.soyle.stories.characterarc.createCharacterDialog.CreateCharacterDialogViewListener
import com.soyle.stories.characterarc.repositories.CharacterRepository
import com.soyle.stories.common.async
import com.soyle.stories.di.DI
import com.soyle.stories.di.get
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Project
import com.soyle.stories.project.ProjectSteps
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import com.soyle.stories.testutils.findComponentsInScope
import javafx.event.ActionEvent
import javafx.geometry.Side
import javafx.scene.control.DialogPane
import javafx.scene.control.MenuItem
import javafx.scene.control.TreeView
import javafx.stage.Window
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import org.testfx.framework.junit5.ApplicationTest
import tornadofx.selectFirst
import java.util.*

object CharacterSteps : ApplicationTest() {

	fun setNumberOfCharactersCreated(double: SoyleStoriesTestDouble, atLeast: Int) {
		ProjectSteps.givenProjectHasBeenOpened(double)
		val currentCount = getNumberOfCharactersCreated(double)
		runBlocking {
			repeat(atLeast - currentCount) {
				whenCharacterIsCreated(double)
			}
		}
	}

	fun getCharactersCreated(double: SoyleStoriesTestDouble): List<Character> {
		val scope = ProjectSteps.getProjectScope(double) ?: return emptyList()
		return runBlocking {
			scope.get<CharacterRepository>().listCharactersInProject(Project.Id(scope.projectId))
		}
	}

	fun getNumberOfCharactersCreated(double: SoyleStoriesTestDouble): Int = getCharactersCreated(double).size

	fun whenCharacterIsCreated(double: SoyleStoriesTestDouble): Character {
		val scope = ProjectSteps.getProjectScope(double)!!
		val repo = scope.get<CharacterRepository>()
		return runBlocking {
			val existingCharacters = repo.listCharactersInProject(Project.Id(scope.projectId)).toSet()
			scope.get<CreateCharacterDialogViewListener>().createCharacter("New Character ${UUID.randomUUID()}")
			repo.listCharactersInProject(Project.Id(scope.projectId)).find { it !in existingCharacters }!!
		}
	}

	fun givenANumberOfCharactersHaveBeenCreated(double: SoyleStoriesTestDouble, atLeast: Int) {
		if (getNumberOfCharactersCreated(double) < atLeast) {
			setNumberOfCharactersCreated(double, atLeast)
		}
		assertTrue(getNumberOfCharactersCreated(double) >= atLeast)
	}

	fun setCharacterListToolOpen(double: SoyleStoriesTestDouble) {
		ProjectSteps.givenProjectHasBeenOpened(double)
		whenCharacterListToolIsOpened(double)
	}

	fun getOpenCharacterListTool(double: SoyleStoriesTestDouble): CharacterList? {
		val projectScope = ProjectSteps.getProjectScope(double) ?: return null
		return findComponentsInScope<CharacterList>(projectScope).singleOrNull()?.takeIf { it.currentStage?.isShowing == true }
	}

	fun isCharacterListToolOpen(double: SoyleStoriesTestDouble): Boolean = getOpenCharacterListTool(double) != null

	fun whenCharacterListToolIsOpened(double: SoyleStoriesTestDouble) {
		val menuItem: MenuItem = ProjectSteps.getMenuItem(double, "tools", "tools_Characters")!!
		interact {
			menuItem.fire()
		}
	}

	fun givenCharacterListToolHasBeenOpened(double: SoyleStoriesTestDouble) {
		if (!isCharacterListToolOpen(double)) {
			setCharacterListToolOpen(double)
		}
		assertTrue(isCharacterListToolOpen(double))
	}

	fun isCharacterListToolShowingEmptyMessage(double: SoyleStoriesTestDouble): Boolean {
		val projectScope = ProjectSteps.getProjectScope(double) ?: return false
		var emptyDisplayIsVisible = false
		interact {
			emptyDisplayIsVisible = projectScope.get<EmptyDisplay>().let {
				it.root.isVisible && it.currentStage != null
			}
		}
		return emptyDisplayIsVisible
	}

	fun isCharacterListToolShowingNumberOfCharacters(double: SoyleStoriesTestDouble, characterCount: Int): Boolean {
		val projectScope = ProjectSteps.getProjectScope(double) ?: return false
		var populatedDisplayIsVisible = false
		var characterListSize = 0
		interact {
			populatedDisplayIsVisible = projectScope.get<PopulatedDisplay>().let {
				it.root.isVisible && it.currentStage != null
			}
			characterListSize = (projectScope.get<PopulatedDisplay>().root.lookup(".tree-view") as TreeView<*>).root.children.size
		}
		return populatedDisplayIsVisible && characterListSize == characterCount
	}

	fun isCharacterListToolShowingCharacter(double: SoyleStoriesTestDouble, character: Character): Boolean {
		val projectScope = ProjectSteps.getProjectScope(double) ?: return false
		var populatedDisplayIsVisible = false
		var characterItemViewModel: CharacterTreeItemViewModel? = null
		interact {
			populatedDisplayIsVisible = projectScope.get<PopulatedDisplay>().let {
				it.root.isVisible && it.currentStage != null
			}
			characterItemViewModel = from(projectScope.get<PopulatedDisplay>().root).lookup(".tree-view").query<TreeView<*>>()
			  .root.children
			  .map { it.value }
			  .filterIsInstance<CharacterTreeItemViewModel>()
			  .find { it.id == character.id.uuid.toString() }
		}
		return populatedDisplayIsVisible && characterItemViewModel != null
	}

	fun whenCharacterIsDeleted(double: SoyleStoriesTestDouble): Character {
		val scope = ProjectSteps.getProjectScope(double)!!
		var firstCharacter: Character? = null
		interact {
			async(scope.applicationScope) {
				firstCharacter = DI.resolve<CharacterRepository>(scope).listCharactersInProject(Project.Id(scope.projectId)).first()
				DI.resolve<CharacterListViewListener>(scope).removeCharacter(firstCharacter!!.id.uuid.toString())
			}
		}
		return firstCharacter!!
	}

	fun setCharacterSelectedInCharacterListTool(double: SoyleStoriesTestDouble) {
		givenANumberOfCharactersHaveBeenCreated(double, 1)
		givenCharacterListToolHasBeenOpened(double)
		val scope = ProjectSteps.getProjectScope(double)!!
		interact {
			from(scope.get<CharacterList>().root).lookup(".tree-view").query<TreeView<*>>().selectFirst()
		}
	}

	fun getCharacterSelectedInCharacterListTool(double: SoyleStoriesTestDouble): CharacterTreeItemViewModel? {
		val projectScope = ProjectSteps.getProjectScope(double) ?: return null
		val locationList = findComponentsInScope<CharacterList>(projectScope).singleOrNull() ?: return null
		var selected: CharacterTreeItemViewModel? = null
		interact {
			selected = from(locationList.root).lookup(".tree-view").query<TreeView<*>>()
			  .selectionModel.selectedItem?.value as? CharacterTreeItemViewModel
		}
		return selected
	}

	fun isCharacterSelectedInCharacterListTool(double: SoyleStoriesTestDouble): Boolean =
	  getCharacterSelectedInCharacterListTool(double) != null

	fun givenCharacterIsSelectedInCharacterListTool(double: SoyleStoriesTestDouble) {
		if (!isCharacterSelectedInCharacterListTool(double)) {
			setCharacterSelectedInCharacterListTool(double)
		}
		assertTrue(isCharacterSelectedInCharacterListTool(double))
	}

	fun setCharacterListToolCharacterContextMenuOpen(double: SoyleStoriesTestDouble) {
		givenCharacterIsSelectedInCharacterListTool(double)
		whenCharacterListToolCharacterContextMenuIsOpened(double)
	}

	fun isCharacterListToolCharacterContextMenuOpen(double: SoyleStoriesTestDouble): Boolean {
		val projectScope = ProjectSteps.getProjectScope(double) ?: return false
		val characterList = findComponentsInScope<CharacterList>(projectScope).singleOrNull() ?: return false
		var isOpen = false
		interact {
			val treeView = (characterList.root.lookup(".tree-view") as TreeView<*>)
			isOpen = treeView.contextMenu?.isShowing ?: false
		}
		return isOpen
	}

	fun whenCharacterListToolCharacterContextMenuIsOpened(double: SoyleStoriesTestDouble) {
		val scope = ProjectSteps.getProjectScope(double)!!
		interact {
			val treeView = (scope.get<CharacterList>().root.lookup(".tree-view") as TreeView<*>)
			treeView.contextMenu!!.show(treeView, Side.TOP, 0.0, 0.0)
		}
	}

	fun givenCharacterListToolCharacterContextMenuHasBeenOpened(double: SoyleStoriesTestDouble) {
		if (!isCharacterListToolCharacterContextMenuOpen(double)) {
			setCharacterListToolCharacterContextMenuOpen(double)
		}
		assertTrue(isCharacterListToolCharacterContextMenuOpen(double))
	}

	fun whenCharacterListToolCharacterContextMenuButtonIsClicked(double: SoyleStoriesTestDouble, menuItemId: String) {
		val projectScope = ProjectSteps.getProjectScope(double) ?: error("Project not yet created")
		val locationList = findComponentsInScope<CharacterList>(projectScope).single()
		interact {
			val treeView = from(locationList.root).lookup(".tree-view").query<TreeView<*>>()
			val menuItem = treeView.contextMenu!!.items.find { it.id == menuItemId }
			  ?: error("No menu item with id $menuItemId")
			menuItem.onAction.handle(ActionEvent())
		}
	}

	fun getOpenConfirmDeleteCharacterDialog(double: SoyleStoriesTestDouble): Window?
	{
		ProjectSteps.getProjectScope(double) ?: return null
		var windows: List<Window> = emptyList()
		interact {
			windows = robotContext().windowFinder.listTargetWindows()
		}
		return windows.find {
			val styleClass = it.scene?.root?.styleClass ?: return@find false

			styleClass.contains("alert") && styleClass.contains("confirmation")
		}
	}

	fun isConfirmDeleteCharacterDialogOpen(double: SoyleStoriesTestDouble): Boolean
	{
		return getOpenConfirmDeleteCharacterDialog(double) != null
	}

	fun isConfirmDeleteCharacterDialogDisplayingNameOf(double: SoyleStoriesTestDouble, character: Character): Boolean
	{
		val window = getOpenConfirmDeleteCharacterDialog(double) ?: return false
		val dialog = window.scene.root as? DialogPane ?: return false
		return dialog.headerText.contains(character.name)
	}

	fun whenCharacterListToolActionBarDeleteButtonIsClicked(double: SoyleStoriesTestDouble)
	{
		val projectScope = ProjectSteps.getProjectScope(double) ?: error("Project not yet created")
		interact {
			from(projectScope.get<ActionBar>().root).lookup("#actionBar_deleteLocation").queryButton().onAction.handle(ActionEvent())
		}
	}

}
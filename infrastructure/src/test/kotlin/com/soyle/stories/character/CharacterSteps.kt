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
import javafx.scene.control.MenuItem
import javafx.scene.control.TreeView
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import org.testfx.framework.junit5.ApplicationTest
import java.util.*

object CharacterSteps : ApplicationTest() {

	fun setNumberOfCharactersCreated(double: SoyleStoriesTestDouble, atLeast: Int)
	{
		ProjectSteps.givenProjectHasBeenOpened(double)
		val currentCount = getNumberOfCharactersCreated(double)
		runBlocking {
			repeat(atLeast - currentCount) {
				whenCharacterIsCreated(double)
			}
		}
	}

	fun getCharactersCreated(double: SoyleStoriesTestDouble): List<Character>
	{
		val scope = ProjectSteps.getProjectScope(double) ?: return emptyList()
		return runBlocking {
			scope.get<CharacterRepository>().listCharactersInProject(Project.Id(scope.projectId))
		}
	}

	fun getNumberOfCharactersCreated(double: SoyleStoriesTestDouble): Int = getCharactersCreated(double).size

	fun whenCharacterIsCreated(double: SoyleStoriesTestDouble): Character
	{
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

	fun setCharacterListToolOpen(double: SoyleStoriesTestDouble)
	{
		ProjectSteps.givenProjectHasBeenOpened(double)
		whenCharacterListToolIsOpened(double)
	}

	fun getOpenCharacterListTool(double: SoyleStoriesTestDouble): CharacterList?
	{
		val projectScope = ProjectSteps.getProjectScope(double) ?: return null
		return findComponentsInScope<CharacterList>(projectScope).singleOrNull()?.takeIf { it.currentStage?.isShowing == true }
	}

	fun isCharacterListToolOpen(double: SoyleStoriesTestDouble): Boolean = getOpenCharacterListTool(double) != null

	fun whenCharacterListToolIsOpened(double: SoyleStoriesTestDouble)
	{
		val menuItem: MenuItem = ProjectSteps.getMenuItem(double, "tools", "tools_Characters")!!
		interact {
			menuItem.fire()
		}
	}

	fun givenCharacterListToolHasBeenOpened(double: SoyleStoriesTestDouble)
	{
		if (! isCharacterListToolOpen(double))
		{
			setCharacterListToolOpen(double)
		}
		assertTrue(isCharacterListToolOpen(double))
	}

	fun isCharacterListToolShowingEmptyMessage(double: SoyleStoriesTestDouble): Boolean
	{
		val projectScope = ProjectSteps.getProjectScope(double) ?: return false
		var emptyDisplayIsVisible = false
		interact {
			emptyDisplayIsVisible = projectScope.get<EmptyDisplay>().let {
				it.root.isVisible && it.currentStage != null
			}
		}
		return emptyDisplayIsVisible
	}

	fun isCharacterListToolShowingNumberOfCharacters(double: SoyleStoriesTestDouble, characterCount: Int): Boolean
	{
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

	fun isCharacterListToolShowingCharacter(double: SoyleStoriesTestDouble, character: Character): Boolean
	{
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

	fun whenCharacterIsDeleted(double: SoyleStoriesTestDouble): Character
	{
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

}
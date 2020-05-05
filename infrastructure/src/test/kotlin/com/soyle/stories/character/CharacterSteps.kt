package com.soyle.stories.character

import com.soyle.stories.character.CharacterSteps.interact
import com.soyle.stories.characterarc.characterList.EmptyDisplay
import com.soyle.stories.characterarc.characterList.PopulatedDisplay
import com.soyle.stories.characterarc.createCharacterDialog.CreateCharacterDialogViewListener
import com.soyle.stories.characterarc.repositories.CharacterRepository
import com.soyle.stories.di.get
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Project
import com.soyle.stories.project.ProjectSteps
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
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
		val scope = ProjectSteps.getProjectScope(double)!!
		val currentCount = getNumberOfCharactersCreated(double)
		runBlocking {
			repeat(atLeast - currentCount) {
				scope.get<CreateCharacterDialogViewListener>().createCharacter("New Character ${UUID.randomUUID()}")
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

	fun givenANumberOfCharactersHaveBeenCreated(double: SoyleStoriesTestDouble, atLeast: Int) {
		if (getNumberOfCharactersCreated(double) < atLeast) {
			setNumberOfCharactersCreated(double, atLeast)
		}
		assertTrue(getNumberOfCharactersCreated(double) >= atLeast)
	}

	fun whenCharacterListToolIsOpened(double: SoyleStoriesTestDouble)
	{
		val menuItem: MenuItem = ProjectSteps.getMenuItem(double, "tools", "tools_Characters")!!
		interact {
			menuItem.fire()
		}
	}

	fun isCharacterListToolShowingEmptyMessage(double: SoyleStoriesTestDouble): Boolean
	{
		val projectScope = ProjectSteps.getProjectScope(double) ?: error("Project not yet created")
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
		val projectScope = ProjectSteps.getProjectScope(double) ?: error("Project not yet created")
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

}
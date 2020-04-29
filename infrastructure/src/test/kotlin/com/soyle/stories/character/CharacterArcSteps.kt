package com.soyle.stories.character

import com.soyle.stories.character.CharacterArcSteps.interact
import com.soyle.stories.characterarc.baseStoryStructure.BaseStoryStructure
import com.soyle.stories.characterarc.baseStoryStructure.BaseStoryStructureScope
import com.soyle.stories.characterarc.planCharacterArcDialog.PlanCharacterArcDialogViewListener
import com.soyle.stories.characterarc.repositories.CharacterArcRepository
import com.soyle.stories.di.get
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.CharacterArc
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Theme
import com.soyle.stories.project.ProjectSteps
import com.soyle.stories.project.layout.openTool.OpenToolController
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import com.soyle.stories.testutils.findComponentsInScope
import javafx.scene.control.ComboBox
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.testfx.framework.junit5.ApplicationTest
import java.util.*

object CharacterArcSteps : ApplicationTest() {

	fun setNumberOfCharacterArcsCreated(double: SoyleStoriesTestDouble, atLeast: Int)
	{
		CharacterSteps.givenANumberOfCharactersHaveBeenCreated(double, 1)
		val character = CharacterSteps.getCharactersCreated(double).first()
		val scope = ProjectSteps.getProjectScope(double)!!
		val currentCount = getNumberOfCharacterArcsCreated(double)
		runBlocking {
			repeat(atLeast - currentCount) {
				scope.get<PlanCharacterArcDialogViewListener>().planCharacterArc(character.id.uuid.toString(), "New Character Arc ${UUID.randomUUID()}")
			}
		}
	}

	fun getCharacterArcsCreated(double: SoyleStoriesTestDouble): List<CharacterArc>
	{
		val scope = ProjectSteps.getProjectScope(double) ?: return emptyList()
		return runBlocking {
			scope.get<CharacterArcRepository>().listAllCharacterArcsInProject(Project.Id(scope.projectId))
		}
	}

	fun getNumberOfCharacterArcsCreated(double: SoyleStoriesTestDouble): Int = getCharacterArcsCreated(double).size

	fun givenANumberOfCharacterArcsHaveBeenCreated(double: SoyleStoriesTestDouble, atLeast: Int) {
		if (getNumberOfCharacterArcsCreated(double) < atLeast) {
			setNumberOfCharacterArcsCreated(double, atLeast)
		}
		Assertions.assertTrue(getNumberOfCharacterArcsCreated(double) >= atLeast)
	}

	fun getOpenBaseStoryStructureTool(double: SoyleStoriesTestDouble, themeId: Theme.Id, characterId: Character.Id): BaseStoryStructure?
	{
		val projectScope = ProjectSteps.getProjectScope(double) ?: return null
		val scope = projectScope.toolScopes
		  .filterIsInstance<BaseStoryStructureScope>()
		  .find { it.themeId == themeId.uuid.toString() && it.characterId == characterId.uuid.toString() }
		  ?: return null
		return findComponentsInScope<BaseStoryStructure>(scope).firstOrNull()
	}

	fun whenBaseStoryStructureToolIsOpened(double: SoyleStoriesTestDouble, themeId: Theme.Id, characterId: Character.Id)
	{
		val scope = ProjectSteps.getProjectScope(double)!!
		interact {
			scope.get<OpenToolController>().openBaseStoryStructureTool(themeId.uuid.toString(), characterId.uuid.toString())
		}
	}

	fun isLocationDropdownDisabledInBaseStoryStructureTool(
	  double: SoyleStoriesTestDouble,
	  themeId: Theme.Id,
	  characterId: Character.Id
	): Boolean
	{
		val baseStoryStructure = getOpenBaseStoryStructureTool(double, themeId, characterId) ?: return false
		var comboBoxes: Set<ComboBox<*>> = emptySet()
		interact {
			comboBoxes = from(baseStoryStructure.root).lookup(".location-select").queryAll()
		}
		return comboBoxes.isNotEmpty() && comboBoxes.all { it.isDisabled }
	}
}
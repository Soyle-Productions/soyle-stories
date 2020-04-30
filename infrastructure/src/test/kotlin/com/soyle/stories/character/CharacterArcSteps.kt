package com.soyle.stories.character

import com.soyle.stories.character.CharacterArcSteps.interact
import com.soyle.stories.characterarc.baseStoryStructure.BaseStoryStructure
import com.soyle.stories.characterarc.baseStoryStructure.BaseStoryStructureScope
import com.soyle.stories.characterarc.planCharacterArcDialog.PlanCharacterArcDialogViewListener
import com.soyle.stories.characterarc.repositories.CharacterArcRepository
import com.soyle.stories.di.get
import com.soyle.stories.entities.*
import com.soyle.stories.location.items.LocationItemViewModel
import com.soyle.stories.project.ProjectSteps
import com.soyle.stories.project.layout.openTool.OpenToolController
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import com.soyle.stories.testutils.findComponentsInScope
import javafx.scene.control.ComboBox
import javafx.scene.input.MouseButton
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertTrue
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

	fun setBaseStoryStructureToolOpen(double: SoyleStoriesTestDouble, themeId: Theme.Id, characterId: Character.Id)
	{
		ProjectSteps.givenProjectHasBeenOpened(double)
		whenBaseStoryStructureToolIsOpened(double, themeId, characterId)
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

	fun isBaseStoryStructureToolOpen(double: SoyleStoriesTestDouble, themeId: Theme.Id, characterId: Character.Id): Boolean =
	  getOpenBaseStoryStructureTool(double, themeId, characterId) != null

	fun whenBaseStoryStructureToolIsOpened(double: SoyleStoriesTestDouble, themeId: Theme.Id, characterId: Character.Id)
	{
		val scope = ProjectSteps.getProjectScope(double)!!
		interact {
			scope.get<OpenToolController>().openBaseStoryStructureTool(themeId.uuid.toString(), characterId.uuid.toString())
		}
	}

	fun givenBaseStoryStructureToolHasBeenOpened(
	  double: SoyleStoriesTestDouble, themeId: Theme.Id, characterId: Character.Id
	) {
		if (! isBaseStoryStructureToolOpen(double, themeId, characterId)) {
			setBaseStoryStructureToolOpen(double, themeId, characterId)
		}
		assertTrue(isBaseStoryStructureToolOpen(double, themeId, characterId))
	}

	fun isLocationDropdownDisabledInBaseStoryStructureTool(
	  double: SoyleStoriesTestDouble, themeId: Theme.Id, characterId: Character.Id
	): Boolean
	{
		val baseStoryStructure = getOpenBaseStoryStructureTool(double, themeId, characterId) ?: return false
		var comboBoxes: Set<ComboBox<*>> = emptySet()
		interact {
			comboBoxes = from(baseStoryStructure.root).lookup(".location-select").queryAll()
		}
		return comboBoxes.isNotEmpty() && comboBoxes.all { it.isDisabled }
	}

	fun getOpenCharacterArcSectionLocationDropDown(
	  double: SoyleStoriesTestDouble, themeId: Theme.Id, characterId: Character.Id
	): ComboBox<*>?
	{
		val baseStoryStructure = getOpenBaseStoryStructureTool(double, themeId, characterId)
		  ?: return null
		var comboBox: ComboBox<*>? = null
		interact {
			val comboBoxes = from(baseStoryStructure.root).lookup(".location-select").queryAll<ComboBox<*>>()
			comboBox = comboBoxes.find { it.isShowing }
		}
		return comboBox
	}

	fun whenCharacterArcSectionLocationDropDownIsClicked(
	  double: SoyleStoriesTestDouble, themeId: Theme.Id, characterId: Character.Id
	) {
		val baseStoryStructure = getOpenBaseStoryStructureTool(double, themeId, characterId)
		  ?: error("base story structure tool not yet opened for $themeId and $characterId")
		interact {
			val comboBoxes = from(baseStoryStructure.root).lookup(".location-select").queryAll<ComboBox<*>>()
			clickOn(comboBoxes.first(), MouseButton.PRIMARY)
		}
	}

	fun isCharacterArcSectionLocationOpenWithAllLocations(
	  double: SoyleStoriesTestDouble, themeId: Theme.Id, characterId: Character.Id, locations: List<Location>
	): Boolean
	{
		val openComboBox = getOpenCharacterArcSectionLocationDropDown(double, themeId, characterId)
		  ?: return false
		val locationMap = locations.associateBy { it.id.uuid.toString() }
		val locationItems = openComboBox.items.filterIsInstance<LocationItemViewModel>()
		return locationItems.map { it.id }.toSet() == locationMap.keys && locationItems.all {
			it.name == locationMap.getValue(it.id).name
		}
	}
}
package com.soyle.stories.character

import com.soyle.stories.UATLogger
import com.soyle.stories.characterarc.baseStoryStructure.BaseStoryStructure
import com.soyle.stories.characterarc.baseStoryStructure.BaseStoryStructureScope
import com.soyle.stories.characterarc.linkLocationToCharacterArcSection.LinkLocationToCharacterArcSectionController
import com.soyle.stories.characterarc.planCharacterArcDialog.PlanCharacterArcDialogViewListener
import com.soyle.stories.di.get
import com.soyle.stories.entities.*
import com.soyle.stories.layout.openTool.OpenToolController
import com.soyle.stories.project.ProjectSteps
import com.soyle.stories.soylestories.SoyleStoriesTestDouble
import com.soyle.stories.testutils.findComponentsInScope
import com.soyle.stories.theme.repositories.CharacterArcRepository
import javafx.event.ActionEvent
import javafx.scene.control.Button
import javafx.scene.control.CheckMenuItem
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertTrue
import org.testfx.framework.junit5.ApplicationTest
import java.util.*

object CharacterArcSteps : ApplicationTest() {

	fun setNumberOfCharacterArcsCreated(double: SoyleStoriesTestDouble, atLeast: Int)
	{
		CharacterDriver.givenANumberOfCharactersHaveBeenCreated(double, 1)
		val character = CharacterDriver.getCharactersCreated(double).first()
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

	fun getCharacterArcSectionsForCharacter(double: SoyleStoriesTestDouble, characterId: Character.Id): List<CharacterArcSection>
	{
		val scope = ProjectSteps.getProjectScope(double) ?: return emptyList()
		return runBlocking {
			scope.get<CharacterArcRepository>().listCharacterArcsForCharacter(characterId).flatMap { it.arcSections }
		}
	}

	fun setBaseStoryStructureToolOpen(double: SoyleStoriesTestDouble, themeId: Theme.Id, characterId: Character.Id)
	{
		ProjectSteps.checkProjectHasBeenOpened(double)
		whenBaseStoryStructureToolIsOpened(double, themeId, characterId)
	}

	fun getOpenBaseStoryStructureTool(double: SoyleStoriesTestDouble, themeId: Theme.Id, characterId: Character.Id): BaseStoryStructure?
	{
		val projectScope = ProjectSteps.getProjectScope(double) ?: return null
		val scope = projectScope.toolScopes
		  .filterIsInstance<BaseStoryStructureScope>()
		  .find { it.type.themeId == themeId.uuid && it.type.characterId == characterId.uuid }
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
		var comboBoxes: Set<Button> = emptySet()
		interact {
			comboBoxes = from(baseStoryStructure.root).lookup(".location-select").queryAll()
		}
		return comboBoxes.isNotEmpty() && comboBoxes.all { it.isDisabled }
	}

	fun setCharacterArcSectionLocationOpen(
	  double: SoyleStoriesTestDouble, themeId: Theme.Id, characterId: Character.Id
	) {
		givenBaseStoryStructureToolHasBeenOpened(double, themeId, characterId)
		whenCharacterArcSectionLocationDropDownIsClicked(double, themeId, characterId)
	}

	fun getCharacterArcSectionLocationDropDown(
	  double: SoyleStoriesTestDouble, themeId: Theme.Id, characterId: Character.Id
	): Button?
	{
		val baseStoryStructure = getOpenBaseStoryStructureTool(double, themeId, characterId)
		  ?: return null
		var locationSelection: Button? = null
		interact {
			val locationSelections = from(baseStoryStructure.root).lookup(".location-select").queryAll<Button>()
			locationSelection = locationSelections.firstOrNull()
		}
		return locationSelection
	}

	fun getOpenCharacterArcSectionLocationDropDown(
	  double: SoyleStoriesTestDouble, themeId: Theme.Id, characterId: Character.Id
	): Button?
	{
		val baseStoryStructure = getOpenBaseStoryStructureTool(double, themeId, characterId)
		  ?: return null
		var locationSelection: Button? = null
		interact {
			val locationSelections = from(baseStoryStructure.root).lookup(".location-select").queryAll<Button>()
			locationSelection = locationSelections.find { it.contextMenu?.isShowing ?: false }
		}
		return locationSelection
	}

	fun whenCharacterArcSectionLocationDropDownIsClicked(
	  double: SoyleStoriesTestDouble, themeId: Theme.Id, characterId: Character.Id
	) {
		val baseStoryStructure = getOpenBaseStoryStructureTool(double, themeId, characterId)
		  ?: error("base story structure tool not yet opened for $themeId and $characterId")
		interact {
			val comboBoxes = from(baseStoryStructure.root).lookup(".location-select").queryAll<Button>()
			comboBoxes.first().onAction.handle(ActionEvent())
		}
	}

	fun isCharacterArcSectionLocationOpen(
	  double: SoyleStoriesTestDouble, themeId: Theme.Id, characterId: Character.Id
	): Boolean {
		return getOpenCharacterArcSectionLocationDropDown(double, themeId, characterId) != null
	}

	fun isCharacterArcSectionLocationOpenWithAllLocations(
	  double: SoyleStoriesTestDouble, themeId: Theme.Id, characterId: Character.Id, locations: List<Location>
	): Boolean
	{
		val openLocationSelection = getOpenCharacterArcSectionLocationDropDown(double, themeId, characterId)
		  ?: return (false).also { UATLogger.log("no open location selection") }
		val locationMap = locations.associateBy { it.id.uuid.toString() }
		val locationItems = openLocationSelection.contextMenu.items.toList()
		val hasAllLocations = locationItems.map { it.id }.toSet() == locationMap.keys
		if (! hasAllLocations) return (false).also { UATLogger.log("not all locations included") }
		val allLocationsMatchNames = locationItems.all {
			it.text == locationMap.getValue(it.id).name
		}
		if (! allLocationsMatchNames) return (false).also { UATLogger.log("not all locations have correct name") }
		return hasAllLocations && allLocationsMatchNames
	}

	fun givenCharacterArcSectionLocationDropDownMenuHasBeenOpened(
	  double: SoyleStoriesTestDouble, themeId: Theme.Id, characterId: Character.Id
	) {
		if (! isCharacterArcSectionLocationOpen(double, themeId, characterId)) {
			setCharacterArcSectionLocationOpen(double, themeId, characterId)
		}
		assertTrue(isCharacterArcSectionLocationOpen(double, themeId, characterId))
	}

	fun whenLocationInCharacterArcSectionLocationDropdownIsSelected(
	  double: SoyleStoriesTestDouble, themeId: Theme.Id, characterId: Character.Id, location: Location
	) {
		val openLocationSelection = getOpenCharacterArcSectionLocationDropDown(double, themeId, characterId)
		  ?: error("Character Arc Section Location Dropdown not yet open")
		interact {
			val locationItem = openLocationSelection.contextMenu.items.find { it.text == location.name }
			  ?: error("no item with text matching location name")
			val onAction = locationItem.onAction
			  ?: error("no registered action for menu item")
			onAction.handle(ActionEvent())
		}
	}

	fun whenCharacterArcSectionLocationDropDownLosesFocus(
	  double: SoyleStoriesTestDouble, themeId: Theme.Id, characterId: Character.Id
	) {
		val openLocationSelection = getOpenCharacterArcSectionLocationDropDown(double, themeId, characterId)
		  ?: error("Character Arc Section Location Dropdown not yet open")
		interact {
			openLocationSelection.contextMenu?.hide()
		}
	}

	fun isCharacterArcSectionLocationDropdownDisplayingLocation(
	  double: SoyleStoriesTestDouble, themeId: Theme.Id, characterId: Character.Id, location: Location
	): Boolean
	{
		val openLocationSelection = getCharacterArcSectionLocationDropDown(double, themeId, characterId)
		  ?: return (false).also { UATLogger.log("Character Arc Section Location Dropdown not yet open") }
		return openLocationSelection.text == location.name
	}

	fun setCharacterArcSectionLinkedToLocation(double: SoyleStoriesTestDouble, sectionId: CharacterArcSection.Id, locationId: Location.Id)
	{
		ProjectSteps.checkProjectHasBeenOpened(double)
		val scope = ProjectSteps.getProjectScope(double)!!
		interact {
			scope.get<LinkLocationToCharacterArcSectionController>().linkLocation(sectionId.uuid.toString(), locationId.uuid.toString())
		}
	}

	fun isCharacterArcSectionLinkedToLocation(double: SoyleStoriesTestDouble, sectionId: CharacterArcSection.Id, locationId: Location.Id): Boolean
	{
		val scope = ProjectSteps.getProjectScope(double) ?: return false
		var section: CharacterArcSection? = null
		runBlocking {
			section = scope.get<CharacterArcRepository>().getCharacterArcContainingArcSection(sectionId)?.arcSections?.find {
				it.id == sectionId
			}
		}
		return section?.linkedLocation == locationId
	}

	fun givenCharacterArcSectionHasALinkedLocation(double: SoyleStoriesTestDouble, section: CharacterArcSection.Id, location: Location.Id) {
		if (! isCharacterArcSectionLinkedToLocation(double, section, location)) {
			setCharacterArcSectionLinkedToLocation(double, section, location)
		}
		assertTrue(isCharacterArcSectionLinkedToLocation(double, section, location))
	}

	fun whenSelectedLocationInCharacterArcSectionLocationDropdownIsDeselected(
	  double: SoyleStoriesTestDouble, themeId: Theme.Id, characterId: Character.Id
	) {
		val openLocationSelection = getOpenCharacterArcSectionLocationDropDown(double, themeId, characterId)
		  ?: error("Character Arc Section Location Dropdown not yet open")
		interact {
			val locationItem = openLocationSelection.contextMenu.items.find { it is CheckMenuItem && it.isSelected }
			  ?: error("no selected item")
			val onAction = locationItem.onAction
			  ?: error("no registered action for menu item")
			onAction.handle(ActionEvent())
		}
	}

	fun isCharacterArcSectionLocationDropdownDisplayingEmptyState(
	  double: SoyleStoriesTestDouble, themeId: Theme.Id, characterId: Character.Id
	): Boolean
	{
		val openLocationSelection = getCharacterArcSectionLocationDropDown(double, themeId, characterId)
		  ?: return (false).also { UATLogger.log("Character Arc Section Location Dropdown not yet open") }
		return openLocationSelection.text == "[link]"
	}
}